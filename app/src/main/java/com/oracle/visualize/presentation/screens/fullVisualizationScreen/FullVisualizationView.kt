package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import android.app.Activity
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController

/**
 * Screen that displays a selected visualization in FullScreen mode.
 *
 * @param visualizationId ID of the visualization to load.
 * @param modifier Modifier for the screen layout.
 * @param viewModel The [FullVisualizationViewModel] that manages the screen state.
 * @param onBackClick Callback to navigate back.
 * @param onThreadsClick Callback to open the threads section.
 */

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun FullVisualizationPage(
    visualizationId: String,
    modifier: Modifier = Modifier,
    viewModel: FullVisualizationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onThreadsClick: (String?) -> Unit = {},
    startInSnippingMode: Boolean = false,
    onSnippingClick: (String?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var snippingBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var chartViewRef by remember { mutableStateOf<View?>(null) }
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()
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

    LaunchedEffect(startInSnippingMode, uiState.isLoading) {
        if (startInSnippingMode) {
            onSnippingClick(visualizationId)
        }
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
                        onSnippingClick(visualizationId)
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Crop,
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
                            visualizationTitle = visualization.title,
                            members = visualization.allUsersSharedWith,
                            onBackClick = onBackClick
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.background)
                                .clipToBounds()
                                .capturable(captureController),
                            contentAlignment = Alignment.Center
                        ) {
                            AndroidView(
                                factory = { context ->
                                    View(context).also { chartViewRef = it }
                                },
                                modifier = Modifier.matchParentSize()
                            )
                            ZoomableChart(
                                chart = chart,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            ) {
                                ChartRenderFullScreen(
                                    chart = chart, showAxisLabels = true, chartColorTheme = uiState.chartColorTheme
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
