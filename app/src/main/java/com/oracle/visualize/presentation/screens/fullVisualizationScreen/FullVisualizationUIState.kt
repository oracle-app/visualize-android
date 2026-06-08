package com.oracle.visualize.presentation.screens.fullVisualizationScreen

import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.VisualizationFullScreen
import com.oracle.visualize.ui.theme.ChartPalette

data class FullVisualizationUIState(
    val isLoading: Boolean = false,
    val visualization: VisualizationFullScreen? = null,
    val chart: Chart<*>? = null,
    val chartColorTheme: ChartPalette = ChartPalette.THEME1,
    val errorMessage: Int? = null
)
