package com.oracle.visualize.data.datasources.dtos

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Data Transfer Object representing a user in the database.
 *
 * @property id The unique identifier for the user document.
 * @property userID The user that receives the notification.
 * @property isRead If it has been read.
 * @property type Type of notification: visualization_shared or comment_reply
 * @property body Message of the notification .
 * @property createdAt The timestamp the notification was created.
 */

class NotificationDTO (
    @DocumentId
    val id: String = "",
    val userID: String = "",
    val isRead: Boolean = false,
    val type: String = "",
    val body: String = "",
    val createdAt:Timestamp = Timestamp.now()
)
