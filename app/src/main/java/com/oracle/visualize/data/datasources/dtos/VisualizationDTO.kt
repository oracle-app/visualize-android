package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class VisualizationDTO(
    @DocumentId
    val id: String = "",
    val authorID: String = "",
    val title: String = "",
    val configJSON: String = "{}",
    val sharedWithUsers: List<String> = emptyList(),
    val sharedWithTeams: List<String> = emptyList(),
    val createdAt: Date = Timestamp.now(),
)
