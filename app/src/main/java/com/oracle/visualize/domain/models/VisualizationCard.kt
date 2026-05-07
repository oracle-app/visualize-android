package com.oracle.visualize.domain.models

import java.util.Date


/**
 * Domain model representing a summary card of a visualization.
 */
data class VisualizationCard(
    val id: String,
    val title: String,
    val author: String,
    val authorID: String,
    val createdAt: Date,
    val configJSON: String,
    val teamsSharedWith: List<Team>,
    val usersSharedWith: List<User>,
    val allUsersSharedWith: List<User>,
    val chart: Chart<*>? = null
)
