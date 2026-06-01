package com.oracle.visualize.domain.usecases.notification

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.NotificationRepository
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notificationID: String): Result<Unit>{
        if (notificationID.isBlank())
            return Result.failure(AppError.GeneralValidationError("Notification ID is empty"))
        return runCatching {
            notificationRepository.markAsRead(notificationID)
        }
    }
}
