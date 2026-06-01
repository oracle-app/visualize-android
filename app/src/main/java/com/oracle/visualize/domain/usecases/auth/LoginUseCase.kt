package com.oracle.visualize.domain.usecases.auth

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject

/**
 * Use case for logging in a user.
 * Validates the email and password before attempting to authenticate via [com.oracle.visualize.domain.repositories.AuthRepository].
 *
 * @property authRepository The repository used for authentication.
 */
class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()

    // Returns Result<AuthUser>
    suspend operator fun invoke(email: String, password: String): AppResult<AuthUser> {
        // 1. Validations using Return
        if (email.isBlank()) {
            return AppResult.Error(
                AppError.AuthValidationError(
                    AppError.AuthField.EMAIL,
                    "Required fields cannot be left blank."
                )
            )
        }

        if (!email.matches(emailRegex)) {
            return AppResult.Error(
                AppError.AuthValidationError(
                    AppError.AuthField.EMAIL,
                    "Please enter a valid email address."))
        }

        if (password.isBlank()) {
            return AppResult.Error(
                AppError.AuthValidationError(
                    AppError.AuthField.PASSWORD,
                    "Required fields cannot be left blank."
                )
            )
        }

        return authRepository.login(email, password)

    }
}
