package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()

    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()){
            return Result.failure(AppError.AuthValidationError(
                AppError.AuthField.EMAIL,
                "Email is required")
            )
        }
        if (!email.matches(emailRegex)) {
            return Result.failure(AppError.AuthValidationError(
                AppError.AuthField.EMAIL,
                "Valid Email required")
            )
        }

        return runCatching {
            authRepository.resetPassword(email)
        }
    }
}
