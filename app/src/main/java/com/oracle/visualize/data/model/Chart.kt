package com.oracle.visualize.data.model

enum class ChartType {
    BAR, LINE, COMBINED
}

data class Chart(
    val id: String,
    val title: String,
    val type: ChartType,
    val isSelected: Boolean = false,
    val data: List<Float> = emptyList() // Mock data for the preview
)
