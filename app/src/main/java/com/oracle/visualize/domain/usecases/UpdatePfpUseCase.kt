package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePfpUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
){
    val uid = authRepository.getCurrentUser()?.uid ?: ""

    suspend operator fun invoke(uri: String): Result<Unit> {
        if (uri.isEmpty()) {
            return Result.failure(AppError.GeneralValidationError("Argument cannot be empty."))
        }

        if (uid == "") {
            return Result.failure(AppError.AuthFailed("User could not be validated."))
        }

        return runCatching {
            userRepository.updatePfp(uid, uri)
        }
    }
}
