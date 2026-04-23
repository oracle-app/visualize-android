package com.oracle.visualize.domain.models

import kotlinx.serialization.json.JsonObject
import java.util.Date

/**
 * Represents a data visualization generated from a dataset.
 */
data class Visualization(
    val id: String,
    val ownerId: String,
    val title: String,
    //val configJson: JsonObject,
    val sharedWith: List<String>,
    val sharedWithGroup: List<String>,
    val commentCount: Int,
    val hasNewActivity: Boolean,
    val createdAt: Date
)
