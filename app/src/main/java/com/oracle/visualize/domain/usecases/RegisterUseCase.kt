package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.exceptions.AppError
import javax.inject.Inject

/**
 * Use case for registering a new user.
 * Performs validation on email and password before calling the [AuthRepository].
 *
 * @property authRepository The repository used for authentication operations.
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()

    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<AuthUser> {

        // 1. Validations using Fail Fast with Result
        if (name.isBlank()){
            return Result.failure(AppError.AuthValidationError(
                AppError.AuthField.NAME,
                "Name is required")
            )
        }

        if (email.isBlank()) {
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
        if (password.isBlank()) {
            return Result.failure(AppError.AuthValidationError(
                AppError.AuthField.PASSWORD,
                "Password is required")
            )
        }
        if (password.length < 6) {
            return Result.failure(
                AppError.AuthValidationError(
                    AppError.AuthField.PASSWORD,
                    "Password must be at least 6 characters")
            )
        }

        if (confirmPassword.isBlank()) {
            return Result.failure(
                AppError.AuthValidationError(
                    AppError.AuthField.CONFIRM_PASSWORD,
                    "Confirm Password is required")
            )
        }

        if (password != confirmPassword){
            return Result.failure(AppError.AuthValidationError(
                AppError.AuthField.CONFIRM_PASSWORD,
                "Passwords mismatch")
            )
        }

        return runCatching {
            authRepository.register(name, email, password)
        }
    }
}
