package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oracle.visualize.presentation.components.ChartRenderFullScreen
import com.oracle.visualize.presentation.screens.fullVisualizationScreen.components.FullVisualizationTopBar
import com.oracle.visualize.R
import com.oracle.visualize.presentation.screens.fullVisualizationScreen.components.ZoomableChart
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.oracle.visualize.presentation.screens.snippingTool.SnippingToolView
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

/**
 * Screen that displays a selected visualization in FullScreen mode.
 *
 * @param visualizationId ID of the visualization to load.
 * @param modifier Modifier for the screen layout.
 * @param viewModel The [FullVisualizationViewModel] that manages the screen state.
 * @param onBackClick Callback to navigate back.
 * @param onThreadsClick Callback to open the threads section.
 */

@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun FullVisualizationPage(
    visualizationId: String,
    modifier: Modifier = Modifier,
    viewModel: FullVisualizationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onThreadsClick: (Uri?) -> Unit = {},
    startInSnippingMode: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var snippingBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var chartViewRef by remember { mutableStateOf<View?>(null) }
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
    val context = LocalContext.current

    LaunchedEffect(visualizationId) {
        viewModel.loadVisualization(visualizationId)
    }

    LaunchedEffect(startInSnippingMode, uiState.isLoading) {
        if (startInSnippingMode && !uiState.isLoading) {

            // Small delay to allow the graph startup animation to play before cropping.

            delay(500)


            val bitmap = captureController.captureAsync().await()
            snippingBitmap = bitmap.asAndroidBitmap()
        }
    }

    snippingBitmap?.let { bitmap ->
        SnippingToolView(
            bitmap = bitmap,
            onDone = { result ->
                viewModel.onSnipCompleted(result)
                val uri = Uri.fromFile(
                    File(context.cacheDir, "snip_${System.currentTimeMillis()}.png").also { file ->
                        file.outputStream().use { result.compress(Bitmap.CompressFormat.PNG, 100, it) }
                    }
                )
                onThreadsClick(uri)
            },
            onCancel = { snippingBitmap = null }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End,
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch{
                            val bitmap = captureController.captureAsync().await()
                            snippingBitmap = bitmap.asAndroidBitmap()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Filled.CropFree,
                        contentDescription = stringResource(R.string.snipping_tool)
                    )
                }

                FloatingActionButton(
                    onClick = {onThreadsClick(null)},
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.ModeComment,
                        contentDescription = stringResource(R.string.threads)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

                uiState.visualization != null -> {
                    val visualization = uiState.visualization!!
                    val chart = uiState.chart!!

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        FullVisualizationTopBar (
                            teamName = visualization.author,
                            visualizationTitle = visualization.title,
                            members = visualization.allUsersSharedWith,
                            onBackClick = onBackClick
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.background)
                                .clipToBounds(),
                            contentAlignment = Alignment.Center
                        ) {
                            AndroidView(
                                factory = { context ->
                                    View(context).also { chartViewRef = it }
                                },
                                modifier = Modifier.matchParentSize()
                            )
                            ZoomableChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    //.heightIn(min = 260.dp, max = 420.dp)
                                    .fillMaxHeight()
                                    .capturable(captureController)
                            ) {
                                ChartRenderFullScreen(chart = chart, showAxisLabels = true)
                            }
                        }
                    }
                }
            }
        }
    }
}
