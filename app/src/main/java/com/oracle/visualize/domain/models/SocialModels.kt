package com.oracle.visualize.domain.models

import java.util.Date

data class Notification (
    val id: String,
    val userId: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: Date,
    val payload: Map<String, Any>
)

data class Group(
    val id: String,
    val ownerId: String,
    val name: String,
    val membersIds: List<Int>
)

data class Comment(
    val id: String,
    val authorId: String,
    val content: String,
    val imageUrl: String?,
    val timestamp: Date
)

data class Thread(
    val id: String,
    val authorId: String,
    val content: String,
    val timestamp: Date
)