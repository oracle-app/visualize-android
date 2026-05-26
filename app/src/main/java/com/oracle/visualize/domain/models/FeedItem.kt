package com.oracle.visualize.domain.models

data class FeedItem(
    val card: VisualizationCard,
    val chart: Chart<*>? = null,
    val isChartLoading: Boolean = true
)
