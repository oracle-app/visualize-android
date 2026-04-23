package com.oracle.visualize.domain.models

import kotlinx.serialization.json.JsonObject
import java.util.Date
import java.util.UUID

/**
 * VisualizationMockData provides a set of dummy [Visualization] objects 
 * used to populate the UI during the development phase before the actual 
 * backend API is integrated.
 */
object VisualizationMockData {
    /**
     * A list of pre-configured visualizations representing typical datasets 
     * like sales, transactions, and commercial performance.
     */
    val visualizations = listOf(
        Visualization(
            id = UUID.randomUUID().toString(),
            ownerId = "user1",
            title = "Commerce Activity: Units Sold vs Total Transactions",
            type = VisualizationType.COMBINED,
            configJson = JsonObject(emptyMap()),
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
            configJson = JsonObject(emptyMap()),
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
            configJson = JsonObject(emptyMap()),
            sharedWith = emptyList(),
            sharedWithGroup = emptyList(),
            commentCount = 0,
            hasNewActivity = false,
            createdAt = Date()
        )
    )
}
