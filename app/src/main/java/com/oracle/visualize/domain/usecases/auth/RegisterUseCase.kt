package com.oracle.visualize.domain.usecases.auth

import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject

/**
 * Use case for registering a new user.
 * Performs validation on email and password before calling the [com.oracle.visualize.domain.repositories.AuthRepository].
 *
 * @property authRepository The repository used for authentication operations.
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()
    private val passwordNumberRegex = ".*[0-9].*".toRegex()
    private val passwordLetterRegex = ".*[a-zA-Z].*".toRegex()

    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): AppResult<AuthUser> {

        // 1. Validations using Fail Fast with Result
        if (name.isBlank()){
            return AppResult.Error(
                AppError.AuthValidationError(
                AppError.AuthField.NAME,
                "Required fields cannot be left blank.")
            )
        }
        if (name.length < 3) {
            return AppResult.Error(
                AppError.AuthValidationError(
                    AppError.AuthField.NAME,
                    "Name must be at least 2 characters."
                )
            )
        }
        if (email.isBlank()) {
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
        if (password.isBlank()) {
            return AppResult.Error(
                AppError.AuthValidationError(
                AppError.AuthField.PASSWORD,
                "Required fields cannot be left blank.")
            )
        }
        if (password.length < 8) {
            return AppResult.Error(
                AppError.AuthValidationError(
                    AppError.AuthField.PASSWORD,
                    "Password must be at least 8 characters")
            )
        }
        if (!password.matches(passwordNumberRegex) || !password.matches(passwordLetterRegex)){
            return AppResult.Error(
                AppError.AuthValidationError(
                    AppError.AuthField.PASSWORD,
                    "Password must include letters and numbers."
                )
            )
        }
        if (confirmPassword.isBlank()) {
            return AppResult.Error(
                AppError.AuthValidationError(
                    AppError.AuthField.CONFIRM_PASSWORD,
                    "Required fields cannot be left blank.")
            )
        }

        if (password != confirmPassword){
            return AppResult.Error(
                AppError.AuthValidationError(
                AppError.AuthField.CONFIRM_PASSWORD,
                "Passwords do not match.")
            )
        }

        return authRepository.register(name, email, password)

    }
}
