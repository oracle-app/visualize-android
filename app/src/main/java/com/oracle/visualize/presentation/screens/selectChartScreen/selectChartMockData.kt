package com.oracle.visualize.presentation.screens.selectChartScreen

import com.oracle.visualize.domain.models.Visualization
import com.google.firebase.Timestamp
import java.util.Date
import java.util.UUID

/**
 * Mock data for development and testing purposes.
 */
object selectChartMockData {
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