package com.oracle.visualize.domain.usecases.auth

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject

class GetUserByIDUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(userID: String): AppResult<User> {
        if (userID.isBlank()) {
            return AppResult.Error(
                AppError.AuthFailed("User ID is empty and thus could not be found.")
            )
        }

        return userRepository.getUserByUserID(userID)
    }
}
