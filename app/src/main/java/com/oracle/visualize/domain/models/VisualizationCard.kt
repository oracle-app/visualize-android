package com.oracle.visualize.domain.models

import com.google.firebase.Timestamp

data class VisualizationCard(
    val id: String,
    val title: String,
    val author: String,
    val createdAt: Timestamp,
    val sharedWith: List<User>,
    val configJSON: String
)
