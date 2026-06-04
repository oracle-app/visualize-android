package com.oracle.visualize.presentation.screens.snippingTool

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.presentation.screens.snippingTool.components.CropOverlay
import com.oracle.visualize.presentation.screens.snippingTool.components.DrawingCanvas
import com.oracle.visualize.presentation.screens.snippingTool.components.DrawingTool
import com.oracle.visualize.presentation.screens.snippingTool.components.SnippingToolActionBar
import com.oracle.visualize.presentation.screens.snippingTool.components.SnippingToolbar
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.oracle.visualize.R
import com.oracle.visualize.presentation.components.ChartRenderFullScreen
import com.oracle.visualize.presentation.screens.fullVisualizationScreen.components.ZoomableChart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun SnippingToolView(
    visualizationId: String,
    onDone: (String) -> Unit,
    onCancel: () ->  Unit,
    modifier: Modifier = Modifier,
    viewModel: SnippingToolViewModel = hiltViewModel()
) {



    // TODO: Right now, the navigation bar appearing and reappearing crooks the crop. Fix this later.

// DisposableEffect(Unit) {
//     val window = (view.context as Activity).window
//     val controller = WindowInsetsControllerCompat(window, view)
//
//     window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//     controller.hide(WindowInsetsCompat.Type.systemBars())
//     controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//
//     onDispose {
//         window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//         controller.show(WindowInsetsCompat.Type.systemBars())
//     }
// }


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, centroid ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += panChange
    }
    val context = LocalContext.current

    LaunchedEffect(visualizationId) {
        viewModel.loadVisualization(visualizationId)
    }

    if (uiState.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleConfirmDialog() },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = { Text(stringResource(R.string.dialog_confirm_title)) },
            text = { Text(stringResource(R.string.dialog_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.toggleConfirmDialog()
                    coroutineScope.launch {
                        val bitmap = viewModel.confirmCrop(graphicsLayer.toImageBitmap().asAndroidBitmap())
                        val uri = withContext(Dispatchers.IO) {
                            File(context.cacheDir, "snip_${System.currentTimeMillis()}.png").also { file ->
                                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                            }.toURI().toString()
                        }
                        onDone(uri)
                    }
                }) {
                    Text(
                        stringResource(R.string.dialog_confirm_yes),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleConfirmDialog() }) {
                    Text(
                        stringResource(R.string.dialog_confirm_no),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    if (uiState.showCancelDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleCancelDialog() },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            title = { Text(stringResource(R.string.dialog_cancel_title)) },
            text = { Text(stringResource(R.string.dialog_cancel_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.toggleCancelDialog()
                    onCancel()
                }) {
                    Text(
                        stringResource(R.string.dialog_cancel_yes),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleCancelDialog() }) {
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
                    if (uiState.cropRect == IntRect.Zero) {
                        viewModel.setCropRect(IntRect(
                            (size.width * 0.1f).toInt(),
                            (size.height * 0.1f).toInt(),
                            (size.width * 0.9f).toInt(),
                            (size.height * 0.9f).toInt()
                        ))
                    }
                }
                .then(
                    if (uiState.isTransformable) Modifier.transformable(state = transformState)
                    else Modifier
                )
                .drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(graphicsLayer)
                }
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = stringResource(uiState.errorMessage!!),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                uiState.chart != null -> {
                    val chart = uiState.chart!!

                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.statusBars.add(WindowInsets(top = 16.dp)))
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SnippingToolActionBar(
                                    onUndo = { viewModel.undo() },
                                    onRedo = { viewModel.redo() }
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { viewModel.toggleCancelDialog() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        )
                                    ) {
                                        Text("Discard")
                                    }
                                    Button(onClick = { viewModel.toggleConfirmDialog() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = MaterialTheme.colorScheme.onSecondary
                                        )) {
                                        Text("Share")
                                    }
                                }
                            }
                        },
                        bottomBar = {
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
                                selectedTool = uiState.selectedTool,
                                cropMode = uiState.isCroppingMode,
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.statusBars)
                            )
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                        ) {
                            ZoomableChart(
                                chart = chart,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                ChartRenderFullScreen(
                                    chart = chart,
                                    showAxisLabels = true,
                                    chartColorTheme = uiState.chartColorTheme
                                )
                            }

                            DrawingCanvas(
                                elements = uiState.elements,
                                selectedTool = uiState.selectedTool ?: DrawingTool.PEN,
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

                            CropOverlay(
                                cropRect = uiState.cropRect,
                                onCropRectChange = { viewModel.setCropRect(it) },
                                isCropDraggable = uiState.isCroppingMode,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
