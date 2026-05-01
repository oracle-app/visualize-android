package com.oracle.visualize.domain.models

import java.util.Date


/**
 * Domain model representing a summary card of a visualization.
 */
data class VisualizationCard(
    val id: String,
    val title: String,
    val author: String,
    val createdAt: Date,
    val sharedWith: List<User>,
    val configJSON: String,
    val chart: Any
)
