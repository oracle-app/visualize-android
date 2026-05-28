package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.VisualizationFullScreen

data class FullVisualizationUIState(
    val isLoading: Boolean = false,
    val visualization: VisualizationFullScreen? = null,
    val chart: Chart<*>? = null,
    val errorMessage: Int? = null
)
