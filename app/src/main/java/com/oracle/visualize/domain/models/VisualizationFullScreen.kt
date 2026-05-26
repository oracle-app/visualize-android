package com.oracle.visualize.domain.models

import java.util.Date

/**
 * Domain model representing a visualization on a full screen view.
 */
data class VisualizationFullScreen(
    val id: String,
    val title: String,
    val author: String,
    val authorID: String,
    val createdAt: Date,
    val teamsSharedWith: List<Team>,
    val usersSharedWith: List<User>,
    val allUsersSharedWith: List<User>,
    val configJSON: String
)
