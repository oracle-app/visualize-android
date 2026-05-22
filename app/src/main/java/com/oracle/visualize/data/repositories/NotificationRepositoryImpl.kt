package com.oracle.visualize.data.repositories

import com.oracle.visualize.data.datasources.NotificationDatasource

import com.oracle.visualize.data.mapper.toDomain
import com.oracle.visualize.domain.exceptions.AppError
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

    override suspend fun getNotificationsForUser(userID: String): List<Notification> {
        return try {
            notificationDS.getNotificationsForUser(userID).map { it.toDomain() }
        }catch (e: Exception){
            if (e is AppError) throw e
            throw AppError.NetworkError("Failed to fetch all notifications: ${e.message}")
        }

    }

}
