package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.ChartDTO
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.HorizontalBarChart
import com.oracle.visualize.domain.models.StackedBarChart
import com.oracle.visualize.domain.models.VerticalBarChart

@Suppress("UNCHECKED_CAST")
fun ChartDTO<*>.toDomain(): Chart<*> {
    return when (this.chartType) {
        "Vertical Bar Chart" -> VerticalBarChart(
            chartTitle = this.chartTitle,
            data = this.data as Map<String, Float>,
            fieldNames = this.fieldNames
        )

        "Horizontal Bar Chart" -> HorizontalBarChart(
            chartTitle = this.chartTitle,
            data = this.data as Map<String, Float>,
            fieldNames = this.fieldNames
        )

        "Stacked Bar Chart" -> StackedBarChart(
            chartTitle = this.chartTitle,
            data = this.data as Map<String, List<Float>>,
            fieldNames = this.fieldNames
        )

        "Stacked Bar Chart" -> StackedBarChart(
            chartTitle = this.chartTitle,
            data = this.data as Map<String, List<Float>>,
            fieldNames = this.fieldNames
        )

        else -> throw IllegalArgumentException("Invalid chart type")
    }
}