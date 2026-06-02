package com.oracle.visualize.domain.usecases.notification

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.NotificationRepository
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notificationID: String): AppResult<Unit>{
        if (notificationID.isBlank())
            return AppResult.Error(AppError.GeneralValidationError("Notification ID is empty"))

        return notificationRepository.markAsRead(notificationID)
    }
}
