package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.exceptions.AppError
import javax.inject.Inject

/**
 * Use case for logging in a user.
 * Validates the email and password before attempting to authenticate via [AuthRepository].
 *
 * @property authRepository The repository used for authentication.
 */
class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()

    // Returns Result<AuthUser>
    suspend operator fun invoke(email: String, password: String): Result<AuthUser> {
        // 1. Validations using Return
        if (email.isBlank()) {
            return Result.failure(
                AppError.AuthValidationError(
                    AppError.AuthField.EMAIL,
                    "Email is required"
                )
            )
        }

        if (!email.matches(emailRegex)) {
            return Result.failure(
                AppError.AuthValidationError(
                    AppError.AuthField.EMAIL,
                    "Valid Email required"))
        }

        if (password.isBlank()) {
            return Result.failure(
                AppError.AuthValidationError(
                    AppError.AuthField.PASSWORD,
                    "Password is required"
                )
            )
        }

        return runCatching {
            authRepository.login(email, password)
        }

    }
}
