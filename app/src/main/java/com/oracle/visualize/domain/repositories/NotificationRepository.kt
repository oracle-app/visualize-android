package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Notification

/**
 * Interface defining the operations for notification management.
 */
interface NotificationRepository {

    suspend fun getNotificationsForUser(userID: String): List<Notification>

}
