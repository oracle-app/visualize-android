package com.oracle.visualize.domain.models

import java.util.Date
import java.util.UUID

/**
 * Mock data for development and testing purposes.
 */
object MockData {
    val visualizations = listOf(
        Visualization(
            id = UUID.randomUUID().toString(),
            ownerId = "user1",
            title = "Commerce Activity: Units Sold vs Total Transactions",
            type = VisualizationType.COMBINED,
            configJson = emptyMap(),
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
            type = VisualizationType.BAR,
            configJson = emptyMap(),
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
            type = VisualizationType.COMBINED,
            configJson = emptyMap(),
            sharedWith = emptyList(),
            sharedWithGroup = emptyList(),
            commentCount = 0,
            hasNewActivity = false,
            createdAt = Date()
        )
    )
}
