package com.oracle.visualize.domain.models

import com.oracle.visualize.domain.models.enums.ChartTypes

sealed class Chart<T>(
    val chartTitle: String,
    val chartType: ChartTypes,
    val data: T,
    val fieldNames: List<Any>
)

class VerticalBarChart(
    chartTitle: String,
    data: Map<String, Float>,
    fieldNames: List<String>
) : Chart<Map<String, Float>>(chartTitle, ChartTypes.VERTICAL_BAR, data, fieldNames)

class HorizontalBarChart(
    chartTitle: String,
    data: Map<String, Float>,
    fieldNames: List<String>
) : Chart<Map<String, Float>>(chartTitle, ChartTypes.HORIZONTAL_BAR, data, fieldNames)

class StackedBarChart(
    chartTitle: String,
    data: Map<String, List<Float>>,
    val stackNames: List<String>
) : Chart<Map<String, List<Float>>>(chartTitle, ChartTypes.STACKED_BAR, data, stackNames)

class LineChart(
    chartTitle: String,
    data: Map<Float, Float>,
    fieldNames: List<String>
) : Chart<Map<Float, Float>>(chartTitle, ChartTypes.LINE, data, fieldNames)

class PieChartModel(
    chartTitle: String,
    data: List<Float>,
    fieldNames: List<String>,
) : Chart<List<Float>>(chartTitle, ChartTypes.PIE, data, fieldNames)

class DonutChart(
    chartTitle: String,
    data: List<Float>,
    fieldNames: List<String>
) : Chart<List<Float>>(chartTitle, ChartTypes.DONUT, data, fieldNames)

class ScatterChart(
    chartTitle: String,
    data: Map<Float, Float>,
    fieldNames: List<String>
) : Chart<Map<Float, Float>>(chartTitle, ChartTypes.SCATTER, data, fieldNames)

class AreaChart(
    chartTitle: String,
    data: Map<Float, List<Float>>,
    val stackNames: List<String>
) : Chart<Map<Float, List<Float>>>(chartTitle, ChartTypes.AREA, data, stackNames)