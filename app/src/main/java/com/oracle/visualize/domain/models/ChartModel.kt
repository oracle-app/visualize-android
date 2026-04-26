package com.oracle.visualize.domain.models

import com.oracle.visualize.domain.models.enums.ChartTypes

abstract class Chart(
    private val chartTitle: String,
    private val chartType: ChartTypes,
    private val data: Any,                   // Cast chart data to any type.
    private val fieldNames: Map<String, String>
) {
    fun getChartTitle(): String {
        return chartTitle
    }

    fun getChartType(): ChartTypes {
        return chartType
    }

    fun getData(): Any {
        return data
    }

    fun getFieldNames(): Map<String, String> {
        return fieldNames
    }
}

class VerticalBarChart(
    chartTitle: String,
    data: Map<String, Float>,
    fieldNames: Map<String, String>
) : Chart(chartTitle, ChartTypes.VERTICAL_BAR, data, fieldNames)

class HorizontalBarChart(
    chartTitle: String,
    data: Map<String, Float>,
    fieldNames: Map<String, String>
) : Chart(chartTitle, ChartTypes.HORIZONTAL_BAR, data, fieldNames)

class StackedBarChart(
    chartTitle: String,
    data: Map<String, List<Float>>,
    fieldNames: Map<String, String>
) : Chart(chartTitle, ChartTypes.STACKED_BAR, data, fieldNames)

class LineChart(
    chartTitle: String,
    data: Map<Float, Float>,
    fieldNames: Map<String, String>
) : Chart(chartTitle, ChartTypes.LINE, data, fieldNames)

class PieChart(
    chartTitle: String,
    data: List<Float>,
    fieldNames: Map<String, String>
) : Chart(chartTitle, ChartTypes.PIE, data, fieldNames)

class DonutChart(
    chartTitle: String,
    data: Map<String, List<Float>>,
    fieldNames: Map<String, String>
) : Chart(chartTitle, ChartTypes.DONUT, data, fieldNames)

class ScatterChart(
    chartTitle: String,
    data: Map<Float, Float>,
    fieldNames: Map<String, String>
) : Chart(chartTitle, ChartTypes.SCATTER, data, fieldNames)

class AreaChart(
    chartTitle: String,
    data: Map<Float, List<Float>>,
    fieldNames: Map<String, String>
) : Chart(chartTitle, ChartTypes.AREA, data, fieldNames)