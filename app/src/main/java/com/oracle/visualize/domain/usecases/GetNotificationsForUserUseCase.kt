package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.Notification
import com.oracle.visualize.domain.repositories.NotificationRepository
import javax.inject.Inject

class GetNotificationsForUserUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
){
    suspend operator fun invoke(userID: String): Result<List<Notification>> {
        if (userID.isBlank())
            return Result.failure(AppError.GeneralValidationError("Notification ID is empty"))
        return runCatching {
            notificationRepository.getNotificationsForUser(userID)
        }
    }

}
