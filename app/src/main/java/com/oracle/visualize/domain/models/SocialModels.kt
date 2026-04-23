package com.oracle.visualize.domain.models

import com.google.firebase.Timestamp

data class Notification (
    val id: String = "",
    val userId: String = "",
    val type: NotificationType = NotificationType.NEW_COMMENT,
    val isRead: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val payload: Map<String, String> = emptyMap()
)

enum class NotificationType {
    NEW_COMMENT, THREAD_UPDATE, GROUP_INVITE
}

data class Group(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val membersIds: List<String> = emptyList()
)

data class Comment(
    val id: String = "",
    val authorID: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val imageUrl: String = "",
    val threads: List<Thread> = emptyList()
)

data class Thread(
    val id: String = "",
    val authorID: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
