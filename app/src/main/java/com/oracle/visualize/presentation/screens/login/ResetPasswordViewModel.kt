package com.oracle.visualize.presentation.screens.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State for the Reset Password flow.
 */
data class ResetPasswordUiState(
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
    val currentStep: ResetPasswordStep = ResetPasswordStep.EMAIL
)

enum class ResetPasswordStep {
    EMAIL, VERIFICATION, NEW_PASSWORD
}

@HiltViewModel
class ResetPasswordViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onCodeChange(code: String) {
        val filtered = code.filter { it.isDigit() }
        if (filtered.length <= 4) {
            _uiState.update { it.copy(code = filtered, codeError = null) }
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun sendResetLink() {
        val state = _uiState.value
        if (state.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = com.oracle.visualize.R.string.reset_password_error_email_required) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000)
            _uiState.update { it.copy(isLoading = false, currentStep = ResetPasswordStep.VERIFICATION) }
        }
    }

    fun verifyCode() {
        if (_uiState.value.code.length < 4) {
            _uiState.update { it.copy(codeError = com.oracle.visualize.R.string.verification_error_code_required) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000)
            _uiState.update { it.copy(isLoading = false, currentStep = ResetPasswordStep.NEW_PASSWORD) }
        }
    }

    fun confirmNewPassword() {
        val state = _uiState.value
        var hasError = false

        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = com.oracle.visualize.R.string.reset_password_error_password_required) }
            hasError = true
        }
        if (state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(confirmPasswordError = com.oracle.visualize.R.string.reset_password_error_confirm_password_required) }
            hasError = true
        } else if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(
                passwordError = com.oracle.visualize.R.string.registration_error_passwords_mismatch,
                confirmPasswordError = com.oracle.visualize.R.string.registration_error_passwords_mismatch
            ) }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1500)
            _uiState.update { it.copy(isLoading = false) }
            // Logic to navigate back to login would be handled in the View via a shared event or similar
        }
    }
}
