package com.oracle.visualize.domain.models

import java.util.Date

data class Visualization (
    val id: String,
    val authorID: String,
    val title: String,
    val configJSON: Map<String, Any>,
    val sharedWithUsers: List<String>,
    val sharedWithTeams: List<String>,
    val createdAt: Date
)
