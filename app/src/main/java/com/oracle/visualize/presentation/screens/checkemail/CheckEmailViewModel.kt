package com.oracle.visualize.presentation.screens.checkemail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI state for the Check Email (OTP verification) screen.
 */
data class CheckEmailUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVerified: Boolean = false,
    val code: String = ""
)

/**
 * ViewModel for the Check Email screen.
 * Manages the OTP code input and simulates verification.
 * No real backend call is made.
 */
class CheckEmailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CheckEmailUiState())
    val uiState: StateFlow<CheckEmailUiState> = _uiState

    /** Updates the OTP code value as the user types. */
    fun updateCode(newCode: String) {
        _uiState.value = _uiState.value.copy(
            code = newCode,
            error = null
        )
    }

    /**
     * Simulates code verification.
     * Requires exactly 4 digits; always succeeds for UI purposes.
     */
    fun verifyCode() {
        val current = _uiState.value.code

        if (current.length < 4) {
            _uiState.value = _uiState.value.copy(
                error = "Please enter the full 4-digit code"
            )
            return
        }

        // Simulate successful verification
        _uiState.value = _uiState.value.copy(isVerified = true)
    }

    /**
     * Simulates resending the verification code.
     * Clears the current code and error state.
     */
    fun resendCode() {
        _uiState.value = CheckEmailUiState()
    }
}
