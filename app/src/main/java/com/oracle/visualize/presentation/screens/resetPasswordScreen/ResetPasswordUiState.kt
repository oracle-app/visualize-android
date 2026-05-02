package com.oracle.visualize.presentation.screens.resetPasswordScreen

/**
 * State for the Reset Password flow.
 */
sealed interface ResetPasswordUiState {
    data class Content(
        val email: String = "",
        val emailError: Int? = null,
        val code: String = "",
        val codeError: Int? = null,
        val password: String = "",
        val passwordError: Int? = null,
        val confirmPassword: String = "",
        val confirmPasswordError: Int? = null,
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val currentStep: ResetPasswordStep = ResetPasswordStep.EMAIL,
        val errorMessage: String? = null
    ) : ResetPasswordUiState

    data object Success : ResetPasswordUiState
}

enum class ResetPasswordStep {
    EMAIL, VERIFICATION, NEW_PASSWORD
}
