package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Data Transfer Object representing a visualization in the database.
 *
 * @property id The unique identifier for the visualization document.
 * @property authorID The unique ID of the user who created the visualization.
 * @property title The title of the visualization.
 * @property configJSON A JSON string containing the configuration for the visualization.
 * @property sharedWithUsers List of user IDs that this visualization is shared with.
 * @property sharedWithTeams List of team IDs that this visualization is shared with.
 * @property createdAt The timestamp when the visualization was created.
 */
data class VisualizationDTO(
    @DocumentId
    val id: String = "",
    val authorID: String = "",
    val title: String = "",
    val configJSON: String = "{}",
    val sharedWithUsers: List<String> = emptyList(),
    val sharedWithTeams: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
)
