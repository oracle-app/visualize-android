package com.oracle.visualize.data.datasources.dtos

/**
 * Data Transfer Object representing a Chart object.
 *
 * @property chartTitle: Title of the chart.
 * @property chartType: Type of chart.
 * @property data: Data of the chart.
 * @property fieldNames: Names of data fields.
 */
data class ChartDTO(
    val chartTitle: String = "",
    val chartType: String = "",
    val data: Any?,
    val fieldNames: List<String> = emptyList()
)
