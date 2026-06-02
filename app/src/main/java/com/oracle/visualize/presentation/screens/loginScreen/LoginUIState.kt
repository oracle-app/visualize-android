package com.oracle.visualize.presentation.screens.loginScreen

import androidx.annotation.StringRes

/**
 * Represents the UI state for the Login screen.
 *
 * @property isLoading Indicates if a login operation is in progress.
 * @property error Contains an error message if the login fails.
 * @property success Indicates if the login was successful.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val passwordErrorRes: Int? = null,
    @StringRes val errorRes: Int? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false
)
