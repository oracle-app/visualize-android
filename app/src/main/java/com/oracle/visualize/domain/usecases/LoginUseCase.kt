package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import android.util.Patterns
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()

    suspend operator fun invoke(email: String, password: String): AuthUser {
        if (email.isBlank()) throw IllegalArgumentException("Email is required")
        if (!email.matches(emailRegex)) throw IllegalArgumentException("Valid Email required")
        if (password.isBlank()) throw IllegalArgumentException("Password is required")
        return authRepository.login(email, password)
    }
}