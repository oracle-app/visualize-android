package com.oracle.visualize.data.repository

import com.oracle.visualize.data.model.Chart
import com.oracle.visualize.data.model.ChartType
import java.util.UUID

/**
 * Repository to manage chart data operations.
 */
class ChartRepository {

    /**
     * Retrieves a list of mock charts for the selection screen.
     */
    fun getCharts(): List<Chart> {
        return listOf(
            Chart(
                id = UUID.randomUUID().toString(),
                title = "Commerce Activity: Units Sold vs Total Transactions",
                type = ChartType.COMBINED,
                data = listOf(10f, 20f, 15f, 30f, 25f, 40f)
            ),
            Chart(
                id = UUID.randomUUID().toString(),
                title = "Commercial Performance Overview: Comparison between Units Sold and Total Transaction Volume",
                type = ChartType.COMBINED,
                data = listOf(5f, 15f, 10f, 25f, 20f, 35f)
            ),
            Chart(
                id = UUID.randomUUID().toString(),
                title = "Units Sold vs Total Transactions",
                type = ChartType.BAR,
                data = listOf(15f, 25f, 20f, 35f, 30f, 45f)
            )
        )
    }
}