package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.oracle.visualize.domain.models.Team
import com.oracle.visualize.domain.models.User
import kotlinx.serialization.json.JsonObject

data class VisualizationDto(
    @DocumentId
    val id: String = "",
    val authorID: String = "",
    val title: String = "",
    val configJSON: String = "{}",
    val sharedWithUsers: List<String> = emptyList(),
    val sharedWithTeams: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val comments: List<CommentDto> = emptyList()
)
