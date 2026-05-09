package com.oracle.visualize.presentation.screens.selectChartScreen

import com.oracle.visualize.domain.models.Visualization
import java.util.Date
import java.util.UUID

/**
 * Mock data for development and testing purposes.
 */
object SelectChartMockData {
    // Strings moved from strings.xml to mock data
    const val READY_TITLE = "Your Visualizations Are Ready!"
    const val READY_SUBTITLE = "We've generated several charts based on your dataset."
    const val SELECTION_PROMPT = "Choose the chart that best represents the insights you want to share."

    val visualizations = listOf(
        Visualization(
            id = UUID.randomUUID().toString(),
            authorID = "user1",
            title = "Commerce Activity: Units Sold vs Total Transactions",
            configJSON = "{}",
            sharedWithUsers = emptyList(),
            sharedWithTeams = emptyList(),
            createdAt = Date()
        ),
        Visualization(
            id = UUID.randomUUID().toString(),
            authorID = "user1",
            title = "Units Sold vs Total Transactions",
            configJSON = "{}",
            sharedWithUsers = emptyList(),
            sharedWithTeams = emptyList(),
            createdAt = Date()
        ),
        Visualization(
            id = UUID.randomUUID().toString(),
            authorID = "user1",
            title = "Commercial Performance Overview: Comparison Between Units Sold and Total Transaction Volume",
            configJSON = "{}",
            sharedWithUsers = emptyList(),
            sharedWithTeams = emptyList(),
            createdAt = Date()
        )
    )
}
