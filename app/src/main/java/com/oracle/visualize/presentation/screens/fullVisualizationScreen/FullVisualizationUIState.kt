package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.VisualizationCard

data class FullVisualizationUIState(
    val isLoading: Boolean = false,
    val visualization: VisualizationCard? = null,
    val chart: Chart<*>? = null,
    val errorMessage: String? = null
)