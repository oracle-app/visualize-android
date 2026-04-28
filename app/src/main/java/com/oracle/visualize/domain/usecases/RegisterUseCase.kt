package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.exceptions.AppError
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for registering a new user.
 * Performs validation on email and password before calling the [AuthRepository].
 *
 * @property authRepository The repository used for authentication operations.
 */
@Singleton
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()

    suspend operator fun invoke(email: String, password: String): Result<AuthUser> {
        // 1. Validaciones usando Fail Fast con Result
        if (email.isBlank()) {
            return Result.failure(AppError.ValidationError("Email is required"))
        }
        if (!email.matches(emailRegex)) {
            return Result.failure(AppError.ValidationError("Valid Email required"))
        }
        if (password.isBlank()) {
            return Result.failure(AppError.ValidationError("Password is required"))
        }
        if (password.length < 6) {
            return Result.failure(AppError.ValidationError("Password must be at least 6 characters"))
        }

        return try {
            val user = authRepository.register(email, password)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}