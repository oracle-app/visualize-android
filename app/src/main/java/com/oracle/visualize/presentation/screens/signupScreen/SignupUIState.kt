package com.oracle.visualize.presentation.screens.signupScreen

import androidx.annotation.StringRes

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    @StringRes val nameErrorRes: Int? = null,
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val passwordErrorRes: Int? = null,
    @StringRes val confirmPasswordErrorRes: Int? = null,
    @StringRes val errorRes: Int? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false
)
