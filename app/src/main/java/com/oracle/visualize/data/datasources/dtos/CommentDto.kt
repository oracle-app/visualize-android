package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class CommentDto(
    val id: String = "",
    val authorID: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val imageUrl: String = "",
    val threads: List<ThreadDto> = emptyList()
)