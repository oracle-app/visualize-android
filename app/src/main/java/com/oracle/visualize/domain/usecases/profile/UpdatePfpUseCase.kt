package com.oracle.visualize.domain.usecases.profile

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePfpUseCase @Inject constructor(
    private val userRepository: UserRepository,
){
    suspend operator fun invoke(userID: String, uri: String): Result<Unit> {
        if (uri.isEmpty()) {
            return Result.failure(AppError.GeneralValidationError("Argument cannot be empty."))
        }

        if (userID == "") {
            return Result.failure(AppError.AuthFailed("User could not be validated."))
        }

        return runCatching {
            userRepository.updatePfp(userID, uri)
        }
    }
}
