package com.oracle.visualize.presentation.screens.newpassword

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI state for the New Password screen.
 */
data class NewPasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

/**
 * ViewModel for the New Password screen.
 * Validates password match and length locally.
 * No real backend call is made — success is simulated.
 */
class NewPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewPasswordUiState())
    val uiState: StateFlow<NewPasswordUiState> = _uiState

    /**
     * Validates the new password fields and simulates a password reset.
     * On valid input, sets [NewPasswordUiState.success] to true.
     */
    fun resetPassword(password: String, confirmPassword: String) {
        val error = when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }

        if (error != null) {
            _uiState.value = NewPasswordUiState(error = error)
            return
        }

        // Simulate success
        _uiState.value = NewPasswordUiState(success = true)
    }

    /** Clears the current error state. */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
