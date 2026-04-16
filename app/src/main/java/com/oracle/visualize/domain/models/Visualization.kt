package com.oracle.visualize.domain.models

import java.util.Date

data class Visualization(
    val id: String,
    val ownerId: String,
    val title: String,
    val type: VisualizationType,
    val configJson: Map<String, Any>,
    val sharedWith: List<String> = emptyList(),
    val sharedWithGroup: List<String> = emptyList(),
    val commentCount: Int = 0,
    val hasNewActivity: Boolean = false,
    val createdAt: Date = Date()
)

enum class VisualizationType {
    BAR,
    LINE,
    PIE,
    SCATTER,
    AREA,
    COMBINED
}
