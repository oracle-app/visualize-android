package com.oracle.visualize.domain.models

import java.util.Date

data class Visualization (
    val id: String,
    val ownerId: String,
    val title: String,
    val configJson: Map<String, Any>,
    val sharedWith: List<String>,
    val sharedWithGroup: List<String>,
    val commentCount: Int,
    val hasNewActivity: Boolean,
    val createdAt: Date
)



