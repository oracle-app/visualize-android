package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class VisualizationDto(
    @DocumentId
    val id: String = "",
    val authorID: String = "",
    val title: String = "",
    val configJSON: Map<String, Any> = emptyMap(),
    val sharedWithUsers: List<String> = emptyList(),
    val sharedWithTeams: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val comments: List<CommentDto> = emptyList()
)
