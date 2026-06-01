package com.oracle.visualize.domain.usecases.notification

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.NotificationRepository
import javax.inject.Inject

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository

){
    suspend operator fun invoke(userID: String): Result<Unit>{
        if (userID.isBlank())
            return Result.failure(AppError.GeneralValidationError("User ID is empty"))
        return runCatching {
            notificationRepository.markAllAsRead(userID)
        }
    }

}
