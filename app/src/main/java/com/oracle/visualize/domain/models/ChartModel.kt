package com.oracle.visualize.domain.models

import androidx.compose.ui.graphics.Color

enum class ChartTypes {
    VERTICAL_BAR, HORIZONTAL_BAR, STACKED_BAR, LINE, PIE, DONUT, SCATTER, AREA
}

data class ChartDataSeries(
    val yValues: List<Float>,
    val xValues: List<Float>,
)

data class Chart(
    val chartTitle: String,
    val chartType: ChartTypes,
    val series: List<ChartDataSeries>,
    val categories: List<String>,
)