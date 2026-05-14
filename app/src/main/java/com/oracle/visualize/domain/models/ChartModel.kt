package com.oracle.visualize.domain.models

import com.oracle.visualize.domain.models.enums.ChartTypes

sealed class Chart<T>(
    val chartTitle: String,
    val chartType: ChartTypes,
    val data: T,
    val metrics: List<String>,
    val fieldNames: List<String>
)

class VerticalBarChart(
    chartTitle: String,
    data: Map<String, Float>,
    metrics: List<String>,
    fieldNames: List<String>
) : Chart<Map<String, Float>>(chartTitle, ChartTypes.VERTICAL_BAR, data, metrics, fieldNames)

class HorizontalBarChart(
    chartTitle: String,
    data: Map<String, Float>,
    metrics: List<String>,
    fieldNames: List<String>
) : Chart<Map<String, Float>>(chartTitle, ChartTypes.HORIZONTAL_BAR, data, metrics, fieldNames)

class StackedBarChart(
    chartTitle: String,
    data: Map<String, List<Float>>,
    metrics: List<String>,
    val stackNames: List<String>
) : Chart<Map<String, List<Float>>>(chartTitle, ChartTypes.STACKED_BAR, data, metrics, stackNames)

class LineChart(
    chartTitle: String,
    data: Map<Float, Float>,
    metrics: List<String>,
    fieldNames: List<String>
) : Chart<Map<Float, Float>>(chartTitle, ChartTypes.LINE, data, metrics, fieldNames)

class PieChartModel(
    chartTitle: String,
    data: List<Float>,
    metrics: List<String>,
    fieldNames: List<String>,
) : Chart<List<Float>>(chartTitle, ChartTypes.PIE, data, metrics, fieldNames)

class DonutChart(
    chartTitle: String,
    data: List<Float>,
    metrics: List<String>,
    fieldNames: List<String>
) : Chart<List<Float>>(chartTitle, ChartTypes.DONUT, data, metrics, fieldNames)

class ScatterChart(
    chartTitle: String,
    data: Map<Float, Float>,
    metrics: List<String>,
    fieldNames: List<String>
) : Chart<Map<Float, Float>>(chartTitle, ChartTypes.SCATTER, data, metrics, fieldNames)

class AreaChart(
    chartTitle: String,
    data: Map<Float, List<Float>>,
    metrics: List<String>,
    val stackNames: List<String>
) : Chart<Map<Float, List<Float>>>(chartTitle, ChartTypes.AREA, data, metrics, stackNames)
