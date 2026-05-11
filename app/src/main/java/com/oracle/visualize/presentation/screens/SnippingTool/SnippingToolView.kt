package com.oracle.visualize.presentation.screens.SnippingTool

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.presentation.screens.SnippingTool.Components.CropOverlay
import com.oracle.visualize.presentation.screens.SnippingTool.Components.DrawingCanvas
import com.oracle.visualize.presentation.screens.SnippingTool.Components.DrawingTool
import com.oracle.visualize.presentation.screens.SnippingTool.Components.SnippingToolActionBar
import com.oracle.visualize.presentation.screens.SnippingTool.Components.SnippingToolbar
import com.tanishranjan.cropkit.CropOptions
import com.tanishranjan.cropkit.ImageCropper
import com.tanishranjan.cropkit.rememberCropController
import com.tanishranjan.cropkit.CropShape
import com.tanishranjan.cropkit.Gridlines
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.oracle.visualize.R

@Composable
fun SnippingToolView(
    bitmap: Bitmap,
    onDone: (Bitmap) -> Unit,
    onCancel: () ->  Unit,
    modifier: Modifier = Modifier,
    viewModel: SnippingToolViewModel = hiltViewModel()
) {
    val view = LocalView.current
    SideEffect {
        val controller = WindowInsetsControllerCompat(
            (view.context as Activity).window,
            view
        )
        controller.hide(WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, centroid ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += panChange
    }

    val isTransformable = !uiState.isDrawingMode && !uiState.isCroppingMode

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = { Text(stringResource(R.string.dialog_confirm_title)) },
            text = { Text(stringResource(R.string.dialog_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    coroutineScope.launch {
                        val fullBitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                        val cropRect = uiState.cropRect
                        val left = cropRect.left.coerceIn(0, fullBitmap.width)
                        val top = cropRect.top.coerceIn(0, fullBitmap.height)
                        val right = cropRect.right.coerceIn(0, fullBitmap.width)
                        val bottom = cropRect.bottom.coerceIn(0, fullBitmap.height)
                        val cropped = Bitmap.createBitmap(fullBitmap, left, top, right - left, bottom - top)
                        onDone(cropped)
                    }
                }) {
                    Text(
                        stringResource(R.string.dialog_confirm_yes),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(
                        stringResource(R.string.dialog_confirm_no),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = { Text(stringResource(R.string.dialog_cancel_title)) },
            text = { Text(stringResource(R.string.dialog_cancel_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    onCancel()
                }) {
                    Text(
                        stringResource(R.string.dialog_cancel_yes),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text(
                        stringResource(R.string.dialog_cancel_no),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .onSizeChanged { size ->
                    val left = (size.width * 0.1f).toInt()
                    val top = (size.height * 0.1f).toInt()
                    val right = (size.width * 0.9f).toInt()
                    val bottom = (size.height * 0.9f).toInt()
                    viewModel.setCropRect(IntRect(left, top, right, bottom))
                }
                .then(
                    if (isTransformable) Modifier.transformable(state = transformState)
                    else Modifier
                )
                .drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(graphicsLayer)
                }
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            )

            DrawingCanvas(
                elements = uiState.elements,
                selectedTool = uiState.selectedTool,
                selectedShape = uiState.selectedShape,
                selectedColor = uiState.selectedColor,
                strokeWidth = uiState.strokeWidth,
                onAddElement = { viewModel.addElement(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                        compositingStrategy = CompositingStrategy.Offscreen
                    },
                isDrawingMode = uiState.isDrawingMode
            )
        }

        CropOverlay(
            cropRect = uiState.cropRect,
            onCropRectChange = { viewModel.setCropRect(it) },
            isCropDraggable = uiState.isCroppingMode,
            modifier = Modifier.fillMaxSize()
        )

        SnippingToolbar(
            onPenClick = { viewModel.selectTool(DrawingTool.PEN) },
            onEraserClick = { viewModel.selectTool(DrawingTool.ERASER) },
            onColorClick = { color -> viewModel.setColor(color.selectedColor) },
            strokeWidth = uiState.strokeWidth,
            onThicknessClick = { viewModel.setStrokeWidth(it) },
            onTextClick = { viewModel.selectTool(DrawingTool.TEXT) },
            onShapeClick = { shape ->
                viewModel.selectTool(DrawingTool.SHAPE)
                viewModel.setShape(shape)
            },
            onCropClick = { viewModel.toggleCrop() },
            selectedColor = uiState.selectedColor,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 48.dp)
        )

        SnippingToolActionBar(
            onUndo = { viewModel.undo() },
            onRedo = { viewModel.redo() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 64.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { showCancelDialog = true },
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.fab_cancel), tint = MaterialTheme.colorScheme.onSecondary)
            }
            SmallFloatingActionButton(
                onClick = { showConfirmDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(R.string.fab_confirm), tint = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}
