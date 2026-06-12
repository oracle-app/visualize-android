package com.oracle.visualize.domain.usecases.profile

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject

class DeleteProfilePictureUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(userID: String): AppResult<Unit> {
        if (userID.isBlank()) {
            return AppResult.Error(
                AppError.AuthFailed("User ID cannot be empty.")
            )
        }

        return userRepository.deleteProfilePicture(userID)
    }
}
