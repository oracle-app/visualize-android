package com.oracle.visualize.domain.usecases.profile

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePfpUseCase @Inject constructor(
    private val userRepository: UserRepository,
){
    suspend operator fun invoke(userID: String, uri: String): AppResult<Unit> {
        if (uri.isEmpty()) {
            return AppResult.Error(AppError.GeneralValidationError("Argument cannot be empty."))
        }

        if (userID == "") {
            return AppResult.Error(AppError.AuthFailed("User could not be validated."))
        }

        return userRepository.updatePfp(userID, uri)
    }
}
