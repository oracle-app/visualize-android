package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

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
