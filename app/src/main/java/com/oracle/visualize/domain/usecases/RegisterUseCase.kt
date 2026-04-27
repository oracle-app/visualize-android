package com.oracle.visualize.domain.usecases

import android.util.Patterns
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()

    suspend operator fun invoke(email: String, password: String): AuthUser {
        if (email.isBlank()) throw IllegalArgumentException("Email is required")
        if (!email.matches(emailRegex)) {
            throw IllegalArgumentException("Valid Email required")
        }
        if (password.isBlank()) throw IllegalArgumentException("Password is required")
        if (password.length < 6) {
            throw IllegalArgumentException("Password must be at least 6 characters")
        }
        return authRepository.register(email,password)
    }
}