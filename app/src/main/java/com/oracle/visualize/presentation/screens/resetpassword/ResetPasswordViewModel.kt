package com.oracle.visualize.presentation.screens.resetpassword

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI state for the Reset Password screen.
 */
data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailSent: Boolean = false
)

/**
 * ViewModel for the Reset Password screen.
 * Validates email format locally and simulates sending a reset email.
 */
class ResetPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState

    /**
     * Validates the email and simulates sending a password reset link.
     * On valid email, sets [ResetPasswordUiState.emailSent] to true.
     */
    fun sendResetEmail(email: String) {
        val error = when {
            email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email"
            else -> null
        }

        if (error != null) {
            _uiState.value = ResetPasswordUiState(error = error)
            return
        }

        // Simulate success
        _uiState.value = ResetPasswordUiState(emailSent = true)
    }

    /** Clears the current error state. */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
