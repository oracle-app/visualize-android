package com.oracle.visualize.presentation.screens.snippingTool

import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Hide Android system bars to allow gesture interaction.
    DisposableEffect(Unit) {
        val activity = context as? Activity ?: (context as? ContextWrapper)?.baseContext as? Activity
        val screenWindow = activity?.window

        if (screenWindow != null) {
            val insetsController = WindowInsetsControllerCompat(screenWindow, screenWindow.decorView)
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            onDispose { insetsController.show(WindowInsetsCompat.Type.systemBars()) }
        } else {
            onDispose {}
        }
    }

    LaunchedEffect(visualizationId) {
        viewModel.loadVisualization(visualizationId)
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
                                    FilledIconButton(
                                        onClick = { viewModel.toggleCancelDialog() },
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        )
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                                    }
                                    FilledIconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                val bitmap = viewModel.confirmCrop(
                                                    graphicsLayer.toImageBitmap().asAndroidBitmap()
                                                )

                                                val uri = withContext(Dispatchers.IO) {
                                                    File(context.cacheDir, "snip_${System.currentTimeMillis()}.png").also { file ->
                                                        file.outputStream().use {
                                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                                        }
                                                    }.toURI().toString()
                                                }

                                                onDone(uri)
                                            }
                                        },
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondary,
                                            contentColor = MaterialTheme.colorScheme.onSecondary
                                        )
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = stringResource(R.string.share))
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            SnippingToolbar(
                                onPenClick = { viewModel.toggleTool(DrawingTool.PEN) },
                                onEraserClick = { viewModel.toggleTool(DrawingTool.ERASER) },
                                onColorClick = { color -> viewModel.setColor(color.selectedColor) },
                                strokeWidth = uiState.strokeWidth,
                                fontSize = uiState.fontSize,
                                onThicknessClick = { viewModel.setStrokeWidth(it) },
                                onTextClick = { viewModel.toggleTool(DrawingTool.TEXT) },
                                onFontSizeChange = {viewModel.setFontSize(it)},
                                onShapeClick = { viewModel.selectTool(DrawingTool.SHAPE) },
                                onShapeSet = { shape -> viewModel.setShape(shape)},
                                onCropClick = { viewModel.toggleCrop() },
                                selectedColor = uiState.selectedColor,
                                selectedTool = uiState.selectedTool,
                                isItalics = uiState.isItalics,
                                italicsToggle = { viewModel.setItalics() },
                                cropMode = uiState.isCroppingMode,
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            )
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .drawWithContent {
                                graphicsLayer.record { this@drawWithContent.drawContent() }
                                drawLayer(graphicsLayer)
                            }
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
                                        compositingStrategy = CompositingStrategy.Offscreen
                                    },
                                isDrawingMode = uiState.isDrawingMode,
                                selectedFontSize = uiState.fontSize.sp,
                                isItalics = uiState.isItalics
                            )

                            CropOverlay(
                                cropRect = uiState.cropRect,
                                onCropRectChange = { viewModel.setCropRect(it) },
                                isCropDraggable = uiState.isCroppingMode,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onSizeChanged { size ->
                                        viewModel.setContainerSize(size)
                                        if (uiState.cropRect == IntRect.Zero) {
                                            viewModel.setCropRect(
                                                IntRect(
                                                    (size.width * 0.1f).toInt(),
                                                    (size.height * 0.1f).toInt(),
                                                    (size.width * 0.9f).toInt(),
                                                    (size.height * 0.9f).toInt()
                                                )
                                            )
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
