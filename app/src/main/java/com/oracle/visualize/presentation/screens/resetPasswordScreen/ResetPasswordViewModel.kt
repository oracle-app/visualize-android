package com.oracle.visualize.presentation.screens.resetPasswordScreen

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
import com.oracle.visualize.R

@HiltViewModel
class ResetPasswordViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ResetPasswordUiState>(ResetPasswordUiState.Content())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    private fun updateContent(update: (ResetPasswordUiState.Content) -> ResetPasswordUiState) {
        _uiState.update { state ->
            if (state is ResetPasswordUiState.Content) update(state) else state
        }
    }

    fun onEmailChange(email: String) {
        updateContent { it.copy(email = email, emailError = null) }
    }

    fun onCodeChange(code: String) {
        val filtered = code.filter { it.isDigit() }
        if (filtered.length <= 4) {
            updateContent { it.copy(code = filtered, codeError = null) }
        }
    }

    fun onPasswordChange(password: String) {
        updateContent { currentState ->
            val nextError = if (currentState.passwordError != null) {
                getPasswordError(password)
            } else {
                null
            }
            currentState.copy(password = password, passwordError = nextError)
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        updateContent { currentState ->
            val nextError = if (currentState.confirmPasswordError != null) {
                if (confirmPassword.isEmpty()) R.string.reset_password_error_confirm_password_required
                else if (currentState.password != confirmPassword) R.string.registration_error_passwords_mismatch
                else null
            } else {
                null
            }
            currentState.copy(confirmPassword = confirmPassword, confirmPasswordError = nextError)
        }
    }

    fun togglePasswordVisibility() {
        updateContent { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        updateContent { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    private fun getPasswordError(password: String): Int? {
        if (password.length < 8) return R.string.error_password_too_short
        if (!password.any { it.isDigit() }) return R.string.error_password_no_digit
        if (!password.any { !it.isLetterOrDigit() }) return R.string.error_password_no_symbol
        return null
    }

    fun sendResetLink() {
        val state = _uiState.value as? ResetPasswordUiState.Content ?: return
        if (state.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            updateContent { it.copy(emailError = R.string.reset_password_error_email_required) }
            return
        }

        viewModelScope.launch {
            updateContent { it.copy(isLoading = true) }
            delay(1000)
            updateContent { it.copy(isLoading = false, currentStep = ResetPasswordStep.VERIFICATION) }
        }
    }

    fun verifyCode() {
        val state = _uiState.value as? ResetPasswordUiState.Content ?: return
        if (state.code.length < 4) {
            updateContent { it.copy(codeError = R.string.verification_error_code_required) }
            return
        }

        viewModelScope.launch {
            updateContent { it.copy(isLoading = true) }
            delay(1000)
            updateContent { it.copy(isLoading = false, currentStep = ResetPasswordStep.NEW_PASSWORD) }
        }
    }

    fun confirmNewPassword() {
        val state = _uiState.value as? ResetPasswordUiState.Content ?: return
        var hasError = false

        val passError = getPasswordError(state.password)
        if (passError != null) {
            updateContent { it.copy(passwordError = passError) }
            hasError = true
        }

        if (state.confirmPassword.isBlank()) {
            updateContent { it.copy(confirmPasswordError = R.string.reset_password_error_confirm_password_required) }
            hasError = true
        } else if (state.password != state.confirmPassword) {
            updateContent { it.copy(
                passwordError = R.string.registration_error_passwords_mismatch,
                confirmPasswordError = R.string.registration_error_passwords_mismatch
            ) }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            updateContent { it.copy(isLoading = true) }
            delay(1500)
            _uiState.value = ResetPasswordUiState.Success
        }
    }
}
