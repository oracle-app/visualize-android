package com.oracle.visualize.presentation.screens.selectChartScreen

import com.oracle.visualize.domain.models.Visualization
import java.util.Date

/**
 * Mock data for development and testing purposes.
 * IDs are fixed strings so the originalTitles map in [SelectChartViewModel]
 * stays stable across recompositions.
 */
object SelectChartMockData {
    val visualizations = listOf(
        Visualization(
            id = "chart-mock-001",
            authorID = "user1",
            title = "Commerce Activity: Units Sold vs Total Transactions",
            configJSON = "{}",
            sharedWithUsers = emptyList(),
            sharedWithTeams = emptyList(),
            createdAt = Date()
        ),
        Visualization(
            id = "chart-mock-002",
            authorID = "user1",
            title = "Units Sold vs Total Transactions",
            configJSON = "{}",
            sharedWithUsers = emptyList(),
            sharedWithTeams = emptyList(),
            createdAt = Date()
        ),
        Visualization(
            id = "chart-mock-003",
            authorID = "user1",
            title = "Commercial Performance Overview: Comparison Between Units Sold and Total Transaction Volume",
            configJSON = "{}",
            sharedWithUsers = emptyList(),
            sharedWithTeams = emptyList(),
            createdAt = Date()
        )
    )
}