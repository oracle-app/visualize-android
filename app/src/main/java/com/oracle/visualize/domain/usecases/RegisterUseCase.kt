package com.oracle.visualize.domain.usecases

import android.util.Patterns
import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthUser {
        if (email.isBlank()) throw IllegalArgumentException("Email is required")
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw IllegalArgumentException("Valid Email required")
        }
        if (password.isBlank()) throw IllegalArgumentException("Password is required")
        if (password.length < 6) {
            throw IllegalArgumentException("Password must be at least 6 characters")
        }
        return repository.register(email,password)
    }
}