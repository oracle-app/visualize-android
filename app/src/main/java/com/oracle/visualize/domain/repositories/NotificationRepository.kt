package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Notification

/**
 * Interface defining the operations for notification management.
 */
interface NotificationRepository {

    suspend fun getNotificationsForUser(userID: String): AppResult<List<Notification>>
    suspend fun markAsRead(notificationID: String): AppResult<Unit>
    suspend fun markAllAsRead(userID: String): AppResult<Unit>
}
