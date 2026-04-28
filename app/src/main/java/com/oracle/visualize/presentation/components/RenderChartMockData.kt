package com.oracle.visualize.presentation.components

import com.oracle.visualize.domain.models.VerticalBarChart

val mockVerticalChart = VerticalBarChart(
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
    fieldNames = mapOf(
        "field1" to "Jan",
        "field2" to "Feb",
        "field3" to "Mar",
        "field4" to "Apr",
        "field5" to "May",
        "field6" to "Jun",
        "field7" to "Jul",
        "field8" to "Aug",
        "field9" to "Sep",
        "field10" to "Oct",
    ),
)