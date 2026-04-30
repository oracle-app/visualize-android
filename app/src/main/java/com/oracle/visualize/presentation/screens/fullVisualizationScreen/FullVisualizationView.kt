package com.oracle.visualize.presentation.screens.fullVisualizationScreen

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
import com.oracle.visualize.presentation.components.ChartRender
import com.oracle.visualize.presentation.screens.fullVisualizationScreen.components.FullVisualizationTopBar
import com.oracle.visualize.presentation.components.mockVerticalChart
import com.oracle.visualize.R


@Composable
fun FullVisualizationPage(
    visualizationId: String,
    modifier: Modifier = Modifier,
    viewModel: FullVisualizationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onThreadsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(visualizationId) {
        viewModel.loadVisualization(visualizationId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    onClick = {
                        // TODO: Implement snipping tool
                    },
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Filled.CropFree,
                        contentDescription = stringResource(R.string.snipping_tool)
                    )
                }

                FloatingActionButton(
                    onClick = onThreadsClick,
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
                        text = uiState.errorMessage ?: "",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                uiState.visualization != null -> {
                    val visualization = uiState.visualization!!

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        FullVisualizationTopBar (
                            teamName = visualization.author,
                            visualizationTitle = visualization.title,
                            members = visualization.sharedWith,
                            onBackClick = onBackClick
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ChartRender(chart = mockVerticalChart)
                        }
                    }
                }
            }
        }
    }
}