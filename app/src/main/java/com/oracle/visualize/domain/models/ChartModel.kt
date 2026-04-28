package com.oracle.visualize.domain.models

import com.oracle.visualize.domain.models.enums.ChartTypes

sealed class Chart<T>(
    val chartTitle: String,
    val chartType: ChartTypes,
    val data: T,
    val fieldNames: Map<String, String>
)

class VerticalBarChart(
    chartTitle: String,
    data: Map<String, Float>,
    fieldNames: Map<String, String>
) : Chart<Map<String, Float>>(chartTitle, ChartTypes.VERTICAL_BAR, data, fieldNames)

class HorizontalBarChart(
    chartTitle: String,
    data: Map<String, Float>,
    fieldNames: Map<String, String>
) : Chart<Map<String, Float>>(chartTitle, ChartTypes.HORIZONTAL_BAR, data, fieldNames)

class StackedBarChart(
    chartTitle: String,
    data: Map<String, List<Float>>,
    fieldNames: Map<String, String>
) : Chart<Map<String, List<Float>>>(chartTitle, ChartTypes.STACKED_BAR, data, fieldNames)

class LineChart(
    chartTitle: String,
    data: Map<Float, Float>,
    fieldNames: Map<String, String>
) : Chart<Map<Float, Float>>(chartTitle, ChartTypes.LINE, data, fieldNames)

class PieChart(
    chartTitle: String,
    data: List<Float>,
    fieldNames: Map<String, String>
) : Chart<List<Float>>(chartTitle, ChartTypes.PIE, data, fieldNames)

class DonutChart(
    chartTitle: String,
    data: Map<String, List<Float>>,
    fieldNames: Map<String, String>
) : Chart<Map<String, List<Float>>>(chartTitle, ChartTypes.DONUT, data, fieldNames)

class ScatterChart(
    chartTitle: String,
    data: Map<Float, Float>,
    fieldNames: Map<String, String>
) : Chart<Map<Float, Float>>(chartTitle, ChartTypes.SCATTER, data, fieldNames)

class AreaChart(
    chartTitle: String,
    data: Map<Float, List<Float>>,
    fieldNames: Map<String, String>
) : Chart<Map<Float, List<Float>>>(chartTitle, ChartTypes.AREA, data, fieldNames)