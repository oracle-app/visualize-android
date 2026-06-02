package com.oracle.visualize.presentation.screens.resetPasswordScreen

import androidx.annotation.StringRes

/**
 * Represents the UI state for the Reset Password screen.
 *
 * @property email The email inputted by the user.
 * @property emailErrorRes Contains an error message specific to the email field.
 * @property errorRes Contains a general error message if the operation fails.
 * @property isLoading Indicates if the reset operation is in progress.
 * @property success Indicates if the reset email was sent successfully.
 */
data class ResetPasswordUiState(
    val email: String = "",
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val errorRes: Int? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false
)
