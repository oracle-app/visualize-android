package com.oracle.visualize.presentation.components

import com.oracle.visualize.domain.models.AreaChart
import com.oracle.visualize.domain.models.DonutChart
import com.oracle.visualize.domain.models.HorizontalBarChart
import com.oracle.visualize.domain.models.LineChart
import com.oracle.visualize.domain.models.PieChartModel
import com.oracle.visualize.domain.models.ScatterChart
import com.oracle.visualize.domain.models.StackedBarChart
import com.oracle.visualize.domain.models.VerticalBarChart

val mockVerticalBarChart = VerticalBarChart(
    chartTitle = "Mock Chart",
    data = mapOf(
        "field1" to 10f,
        "field2" to 20f,
        "field3" to 30f,
        "field4" to 40f,
        "field5" to 50f,
        "field6" to 10f,
        "field7" to 20f,
        "field8" to 30f,
        "field9" to 40f,
        "field10" to 50f,
    ),
    fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct"),
)

val mockHorizontalBarChart = HorizontalBarChart(
    chartTitle = "Mock Chart",
    data = mapOf(
        "field1" to 10f,
        "field2" to 20f,
        "field3" to 30f,
        "field4" to 40f,
        "field5" to 50f,
        "field6" to 10f,
        "field7" to 20f,
        "field8" to 30f,
        "field9" to 40f,
        "field10" to 50f,
    ),
    fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct"),
)

val mockStackedBarChart = StackedBarChart(
    chartTitle = "Mock Chart",
    data = mapOf(
        "1990" to listOf(10f, 20f, 30f),
        "2000" to listOf(10f, 20f, 30f),
        "2010" to listOf(20f, 22f, 32f),
    ),
    stackNames = listOf("A", "B", "C")
)

val mockLineChart = LineChart(
    chartTitle = "Mock Chart",
    data = mapOf(
        10f to 10f,
        20f to 20f,
        30f to 35f,
        40f to 27f,
        50f to 52f
    ),
    fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May")
)

val mockScatterChart = ScatterChart(
    chartTitle = "Mock Chart",
    data = mapOf(
        10f to 10f,
        20f to 20f,
        30f to 35f,
        40f to 27f,
        50f to 52f
    ),
    fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May")
)

val mockPieChart = PieChartModel(
    chartTitle = "Mock Chart",
    data = listOf(
        10f,
        20f,
        30f,
        40f,
        50f
    ),
    fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May")
)

val mockDonutChart = DonutChart(
    chartTitle = "Mock Chart",
    data = listOf(
        10f,
        20f,
        30f,
        40f,
        50f
    ),
    fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May")
)

val mockAreaChart = AreaChart(
    chartTitle = "Mock Chart",
    data = mapOf(
        4f to listOf(10f, 20f, 30f),
        10f to listOf(20f, 22f, 28f),
        20f to listOf(15f, 25f, 35f)
    ),
    stackNames = listOf("A", "B", "C")
)