package com.oracle.visualize.domain.models

import com.google.firebase.Timestamp
import java.sql.Date

data class VisualizationCard(
    val id: String,
    val title: String,
    val author: String,
    val createdAt: Date,
    val sharedWith: List<User>,
    val configJSON: String
)
