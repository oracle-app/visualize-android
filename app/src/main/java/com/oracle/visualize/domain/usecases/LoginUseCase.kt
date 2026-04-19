package com.oracle.visualize.domain.usecases

import com.oracle.visualize.domain.models.AuthUser
import com.oracle.visualize.domain.repositories.AuthRepository
import android.util.Patterns

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthUser {
        if (email.isBlank()) throw IllegalArgumentException("Email is required")
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) throw IllegalArgumentException("Valid Email required")
        if (password.isBlank()) throw IllegalArgumentException("Password is required")
        return repository.login(email, password)
    }
}