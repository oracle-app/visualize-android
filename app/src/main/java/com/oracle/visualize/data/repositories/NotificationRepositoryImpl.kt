package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.NotificationDatasource

import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.core.utils.safeApiCall
import com.oracle.visualize.domain.models.Notification
import com.oracle.visualize.domain.repositories.NotificationRepository
import javax.inject.Inject


/**
 * Implementation of [NotificationRepository] that coordinates notification-related data operations.
 * It uses [NotificationDatasource] to fetch complete  information,*
 */
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDS: NotificationDatasource
): NotificationRepository{

    override suspend fun getNotificationsForUser(userID: String): AppResult<List<Notification>> {
        return safeApiCall {
            notificationDS.getNotificationsForUser(userID).map { it.toDomain() }
        }
    }

    override suspend fun markAllAsRead(userID: String): AppResult<Unit> {
        return safeApiCall {
            notificationDS.markAllNotificationsAsRead(userID)
        }
    }

    override suspend fun markAsRead(notificationID: String): AppResult<Unit> {
        return safeApiCall {
            notificationDS.markNotificationAsRead(notificationID)
        }
    }

}
