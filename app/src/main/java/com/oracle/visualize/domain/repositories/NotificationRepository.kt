package com.oracle.visualize.domain.repositories

import com.oracle.visualize.domain.models.Notification
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the operations for managing notifications.
 */
interface NotificationRepository {
    /**
     * Retrieves a stream of notifications.
     * 
     * @return A [Flow] of lists of [Notification].
     */
    fun getNotifications(): Flow<List<Notification>>
}
