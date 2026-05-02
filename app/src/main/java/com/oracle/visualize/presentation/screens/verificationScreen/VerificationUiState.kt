package com.oracle.visualize.presentation.screens.verificationScreen

/**
 * State for the Verification screen.
 */
sealed interface VerificationUiState {
    data class Content(
        val code: String = "",
        val codeError: Int? = null,
        val resendTimer: Int = 0,
        val isResendEnabled: Boolean = true,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val showResendWaitMessage: Boolean = false
    ) : VerificationUiState

    data object Success : VerificationUiState
}
