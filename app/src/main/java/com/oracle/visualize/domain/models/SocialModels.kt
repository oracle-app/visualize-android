package com.oracle.visualize.domain.models

import java.util.Date

data class Notification (
    val id: String,
    val userId: String,
    val type: NotificationType,
    val isRead: Boolean,
    val createdAt: Date,
    val payload: Map<String, String>
)

enum class NotificationType {
    NEW_COMMENT, THREAD_UPDATE, GROUP_INVITE
}

data class Group(
    val id: String,
    val ownerId: String,
    val name: String,
    val membersIds: List<String>
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