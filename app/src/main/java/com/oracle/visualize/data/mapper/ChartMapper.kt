package com.oracle.visualize.data.mapper

import com.oracle.visualize.data.datasources.dtos.ChartDTO
import com.oracle.visualize.domain.models.*
import com.oracle.visualize.domain.models.enums.ChartTypes

/**
 * Extension function to map [ChartDTO] to [Chart] domain model, returning a specific
 * chart implementation based on its type.
 *
 * @return A [Chart] object.
 */
@Suppress("UNCHECKED_CAST")
fun ChartDTO.toDomain(): Chart<*> {
    return try {
        val type = runCatching {
            ChartTypes.fromTypeName(this.chartType)
        }.getOrElse {
            runCatching { ChartTypes.valueOf(this.chartType) }.getOrDefault(ChartTypes.VERTICAL_BAR)
        }

        val stringFieldNames = fieldNames.filterIsInstance<String>()

        when (type) {
            ChartTypes.VERTICAL_BAR -> VerticalBarChart(
                chartTitle = chartTitle,
                data = (data as? Map<*, *>)?.entries?.associate {
                    it.key.toString() to (it.value?.toString()?.toFloatOrNull() ?: 0f)
                } ?: emptyMap(),
                fieldNames = stringFieldNames
            )

            ChartTypes.HORIZONTAL_BAR -> HorizontalBarChart(
                chartTitle = chartTitle,
                data = (data as? Map<*, *>)?.entries?.associate {
                    it.key.toString() to (it.value?.toString()?.toFloatOrNull() ?: 0f)
                } ?: emptyMap(),
                fieldNames = stringFieldNames
            )

            ChartTypes.STACKED_BAR -> StackedBarChart(
                chartTitle = chartTitle,
                data = (data as? Map<*, *>)?.entries?.associate { entry ->
                    entry.key.toString() to (entry.value as? List<*>)?.map {
                        it?.toString()?.toFloatOrNull() ?: 0f
                    }.orEmpty()
                } ?: emptyMap(),
                stackNames = stringFieldNames
            )

            ChartTypes.LINE -> LineChart(
                chartTitle = chartTitle,
                data = (data as? Map<*, *>)?.entries?.associate {
                    (it.key.toString().toFloatOrNull() ?: 0f) to (it.value?.toString()?.toFloatOrNull() ?: 0f)
                } ?: emptyMap(),
                fieldNames = stringFieldNames
            )

            ChartTypes.SCATTER -> ScatterChart(
                chartTitle = chartTitle,
                data = (data as? Map<*, *>)?.entries?.associate {
                    (it.key.toString().toFloatOrNull() ?: 0f) to (it.value?.toString()?.toFloatOrNull() ?: 0f)
                } ?: emptyMap(),
                fieldNames = stringFieldNames
            )

            ChartTypes.PIE -> PieChartModel(
                chartTitle = chartTitle,
                data = (data as? List<*>)?.map { it?.toString()?.toFloatOrNull() ?: 0f } ?: emptyList(),
                fieldNames = stringFieldNames
            )

            ChartTypes.DONUT -> DonutChart(
                chartTitle = chartTitle,
                data = (data as? List<*>)?.map { it?.toString()?.toFloatOrNull() ?: 0f } ?: emptyList(),
                fieldNames = stringFieldNames
            )

            ChartTypes.AREA -> AreaChart(
                chartTitle = chartTitle,
                data = (data as? Map<*, *>)?.entries?.associate { entry ->
                    (entry.key.toString().toFloatOrNull() ?: 0f) to (entry.value as? List<*>)?.map {
                        it?.toString()?.toFloatOrNull() ?: 0f
                    }.orEmpty()
                } ?: emptyMap(),
                stackNames = stringFieldNames
            )
        }
    } catch (e: Throwable) {
        VerticalBarChart(
            chartTitle = "",
            data = emptyMap(),
            fieldNames = emptyList()
        )
    }
}
