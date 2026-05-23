package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp

data class ThreadDTO(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorImageUrl: String? = null,
    val content: String = "",
    val imageUrl: String? = null,
    val createdAt: Timestamp = Timestamp.now()
)
