package com.oracle.visualize.data.mapper

import com.google.firebase.Timestamp
import com.oracle.visualize.data.datasources.dtos.NotificationDTO
import com.oracle.visualize.domain.models.Notification

/**
 * Extension function to map [NotificationDTO] to [Notification]
 * @return a [Notification] object
 */

fun NotificationDTO.onDomain(): Notification = Notification(
    id = id,
    isRead = isRead,
    body = body,
    createdAt = createdAt.toDate(),
)


/**
 * Extension function to map [Notification] to [NotificationDTO]
 * @return a [NotificationDTO] object
 */

fun Notification.toNotificationDTO(): NotificationDTO = NotificationDTO(
    id = id,
    isRead = isRead,
    body = body,
    createdAt = Timestamp(createdAt)
)
