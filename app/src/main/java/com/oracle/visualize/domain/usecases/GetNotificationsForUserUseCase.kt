package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.Notification
import com.oracle.visualize.domain.repositories.NotificationRepository
import javax.inject.Inject

class GetNotificationsForUserUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
){
    suspend operator fun invoke(userID: String): Result<List<Notification>> {
        return runCatching {
            notificationRepository.getNotificationsForUser(userID)
        }
    }

}
