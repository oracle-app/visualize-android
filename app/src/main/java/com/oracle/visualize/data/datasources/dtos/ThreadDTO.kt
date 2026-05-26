package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp

data class ThreadDTO(
    val id: String = "",
    val authorID: String = "",
    val authorName: String = "",
    val authorAvatarURL: String? = null,
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now()
)
