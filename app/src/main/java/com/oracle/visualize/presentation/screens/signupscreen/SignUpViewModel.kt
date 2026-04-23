package com.oracle.visualize.presentation.screens.signupscreen

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI state for the Sign Up screen.
 * Holds loading, error, and success flags.
 */
data class SignUpUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

/**
 * ViewModel for the Sign Up screen.
 * Performs local-only validation on name, email, password, and confirm password.
 * No real backend call is made — success is simulated on valid input.
 */
class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    /**
     * Validates all sign-up fields locally and updates UI state.
     * On valid input, sets [SignUpUiState.success] to true.
     */
    fun signUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val error = when {
            name.isBlank() -> "Name is required"
            email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email"
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }

        if (error != null) {
            _uiState.value = SignUpUiState(error = error)
            return
        }

        // Simulate success (no real backend)
        _uiState.value = SignUpUiState(success = true)
    }

    /** Clears the current error state. */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
