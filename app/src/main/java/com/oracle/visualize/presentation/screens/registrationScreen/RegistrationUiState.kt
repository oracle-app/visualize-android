package com.oracle.visualize.presentation.screens.registrationScreen

/**
 * State for the Registration screen.
 */
sealed interface RegistrationUiState {
    data class Content(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val nameError: Int? = null,
        val emailError: Int? = null,
        val passwordError: Int? = null,
        val confirmPasswordError: Int? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false
    ) : RegistrationUiState

    data object Success : RegistrationUiState
}
