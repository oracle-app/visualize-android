package com.oracle.visualize.presentation.screens.selectChartScreen

import com.oracle.visualize.domain.models.Visualization
import java.util.Date
import java.util.UUID

/**
 * Mock data for development and testing purposes.
 */
object selectChartMockData {
    val visualizations = listOf(
        Visualization(
            id = UUID.randomUUID().toString(),
            ownerId = "user1",
            title = "Commerce Activity: Units Sold vs Total Transactions",
            //configJson = emptyMap(),
            sharedWith = emptyList(),
            sharedWithGroup = emptyList(),
            commentCount = 0,
            hasNewActivity = false,
            createdAt = Date()
        ),
        Visualization(
            id = UUID.randomUUID().toString(),
            ownerId = "user1",
            title = "Units Sold vs Total Transactions",
            //configJson = emptyMap(),
            sharedWith = emptyList(),
            sharedWithGroup = emptyList(),
            commentCount = 0,
            hasNewActivity = false,
            createdAt = Date()
        ),
        Visualization(
            id = UUID.randomUUID().toString(),
            ownerId = "user1",
            title = "Commercial Performance Overview: Comparison Between Units Sold and Total Transaction Volume",
            //configJson = emptyMap(),
            sharedWith = emptyList(),
            sharedWithGroup = emptyList(),
            commentCount = 0,
            hasNewActivity = false,
            createdAt = Date()
        )
    )
}