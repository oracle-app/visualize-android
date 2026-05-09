package com.oracle.visualize.data.datasources

import com.oracle.visualize.data.datasources.dtos.ChartDTO
import com.oracle.visualize.domain.exceptions.AppError
import javax.inject.Inject

/**
 * Data source for chart-related operations.
 */
class ChartDataSource @Inject constructor(){
    /**
     * Return a mock chart based on its type.
     *
     * TODO: Change when the chart generator microservice API endpoints are available.
     *
     * @param chartType The type of the chart.
     * @throws AppError.UnavailableMockData If the chart type is not supported.
     * @throws AppError.NetworkError If connection to the repository fails.
     */
    suspend fun getMockChart(chartType: String): ChartDTO {
        return try {
            when (chartType) {
                "Vertical Bar Chart" -> {
                    ChartDTO(
                        chartTitle = "Mock Chart",
                        chartType = "Vertical Bar Chart",
                        data = mapOf(
                            "field1" to 10f, "field2" to 20f, "field3" to 30f, "field4" to 40f, "field5" to 50f, "field6" to 10f,
                            "field7" to 20f, "field8" to 30f, "field9" to 40f, "field10" to 50f
                        ),
                        fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct")
                    )
                }

                "Horizontal Bar Chart" -> {
                    ChartDTO(
                        chartTitle = "Horizontal Bar Chart",
                        chartType = "Horizontal Bar Chart",
                        data = mapOf(
                            "field1" to 10f, "field2" to 20f, "field3" to 30f, "field4" to 40f, "field5" to 50f, "field6" to 10f,
                            "field7" to 20f, "field8" to 30f, "field9" to 40f, "field10" to 50f,
                        ),
                        fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct")
                    )
                }

                "Stacked Bar Chart" -> {
                    ChartDTO(
                        chartTitle = "Stacked Bar Chart",
                        chartType = "Stacked Bar Chart",
                        data = mapOf("1990" to listOf(10f, 20f, 30f), "2000" to listOf(10f, 20f, 30f), "2010" to listOf(20f, 22f, 32f)),
                        fieldNames = listOf("A", "B", "C")
                    )
                }

                "Line" -> {
                    ChartDTO(
                        chartTitle = "Line Chart",
                        chartType = "Line",
                        data = mapOf(10f to 10f, 20f to 20f, 30f to 35f, 40f to 27f, 50f to 52f),
                        fieldNames = listOf("Currency (USD)")
                    )
                }

                "Scatter" -> {
                    ChartDTO(
                        chartTitle = "Scatter Chart",
                        chartType = "Scatter",
                        data = mapOf(10f to 10f, 20f to 20f, 30f to 35f, 40f to 27f, 50f to 52f),
                        fieldNames = listOf("Currency (USD)")
                    )
                }

                "Pie" -> {
                    ChartDTO(
                        chartTitle = "Pie Chart",
                        chartType = "Pie",
                        data = listOf(10f, 20f, 30f, 40f, 50f),
                        fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May")
                    )
                }

                "Donut" -> {
                    ChartDTO(
                        chartTitle = "Donut Chart",
                        chartType = "Donut",
                        data = listOf(10f, 20f, 30f, 40f, 50f),
                        fieldNames = listOf("Jan", "Feb", "Mar", "Apr", "May")
                    )
                }

                "Area" -> {
                    ChartDTO(
                        chartTitle = "Area Chart",
                        chartType = "Area",
                        data = mapOf(4f to listOf(10f, 20f, 30f), 10f to listOf(20f, 22f, 28f), 20f to listOf(15f, 25f, 35f)),
                        fieldNames = listOf("A", "B", "C")
                    )
                }

                else -> {
                    throw AppError.UnavailableMockData("Mock of $chartType chart is unavailable")
                }
            }
        } catch (ex: Exception) {
            if (ex is AppError) throw ex
            throw AppError.NetworkError("Failed to get mock chart: ${ex.message}")
        }
    }
}
