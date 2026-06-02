package com.oracle.visualize.domain.usecases.notification

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.NotificationRepository
import javax.inject.Inject

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository

){
    suspend operator fun invoke(userID: String): AppResult<Unit>{
        if (userID.isBlank())
            return AppResult.Error(AppError.GeneralValidationError("User ID is empty"))

        return notificationRepository.markAllAsRead(userID)
    }

}
