package com.oracle.visualize.domain.usecases.notification

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.Notification
import com.oracle.visualize.domain.repositories.NotificationRepository
import javax.inject.Inject

class GetNotificationsForUserUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
){
    suspend operator fun invoke(userID: String): AppResult<List<Notification>> {
        if (userID.isBlank())
            return AppResult.Error(AppError.GeneralValidationError("Notification ID is empty"))

        return notificationRepository.getNotificationsForUser(userID)
    }

}
