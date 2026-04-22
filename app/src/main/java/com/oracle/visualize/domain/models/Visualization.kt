package com.oracle.visualize.domain.models

import kotlinx.serialization.json.JsonObject
import java.util.Date

data class Visualization (
    val id: String,
    val authorID: String,
    val title: String,
    val configJSON: JsonObject,
    val sharedWithUsers: List<String>,
    val sharedWithTeams: List<String>,
    val createdAt: Date,
    val comments: List<Comment>
)
