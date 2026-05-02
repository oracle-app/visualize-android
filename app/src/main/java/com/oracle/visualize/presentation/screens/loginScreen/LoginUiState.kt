package com.oracle.visualize.presentation.screens.loginScreen

/**
 * State for the Login screen.
 */
sealed interface LoginUiState {
    data class Content(
        val email: String = "",
        val password: String = "",
        val emailError: Int? = null,
        val passwordError: Int? = null,
        val isPasswordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : LoginUiState

    data object Success : LoginUiState
}
