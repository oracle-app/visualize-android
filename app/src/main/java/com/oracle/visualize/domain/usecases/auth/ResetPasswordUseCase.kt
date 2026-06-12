package com.oracle.visualize.domain.usecases.auth

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()

    suspend operator fun invoke(email: String): AppResult<Unit> {
        if (email.isBlank()){
            return AppResult.Error(
                AppError.AuthValidationError(
                AppError.AuthField.EMAIL,
                "Required fields cannot be left blank.")
            )
        }
        if (!email.matches(emailRegex)) {
            return AppResult.Error(
                AppError.AuthValidationError(
                AppError.AuthField.EMAIL,
                "Please enter a valid email address.")
            )
        }

        return authRepository.resetPassword(email)
    }
}
