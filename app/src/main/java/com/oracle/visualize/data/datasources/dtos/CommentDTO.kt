package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp

data class CommentDTO(
    val id: String = "",
    val authorID: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val imageURL: String? = null
)
