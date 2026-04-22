package com.oracle.visualize.domain.models

import org.json.JSONObject
import java.util.Date

data class Visualization (
    val id: String,
    val authorID: String,
    val title: String,
    val configJSON: JSONObject,
    val sharedWithUsers: List<String>,
    val sharedWithTeams: List<String>,
    val createdAt: Date,
    val comments: List<Comment>
)
