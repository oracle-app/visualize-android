package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject

class DeleteProfilePictureUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(userID: String): Result<Unit> {
        if (userID.isBlank()) {
            return Result.failure(
                AppError.AuthFailed("User ID cannot be empty.")
            )
        }

        return runCatching {
            userRepository.deleteProfilePicture(userID)
        }
    }
}
