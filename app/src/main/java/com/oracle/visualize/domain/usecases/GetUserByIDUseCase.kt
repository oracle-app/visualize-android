package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject

class GetUserByIDUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(userID: String): Result<User> {
        if (userID.isBlank()) {
            return Result.failure(
                AppError.AuthFailed("User ID is empty and thus could not be found.")
            )
        }

        return runCatching {
            userRepository.getUserByUserID(userID)
        }
    }
}
