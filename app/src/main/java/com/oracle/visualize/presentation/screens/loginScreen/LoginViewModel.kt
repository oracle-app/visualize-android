package com.oracle.visualize.presentation.screens.loginScreen

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.oracle.visualize.R

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Content())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private fun updateContent(update: (LoginUiState.Content) -> LoginUiState) {
        _uiState.update { state ->
            if (state is LoginUiState.Content) update(state) else state
        }
    }

    fun onEmailChange(email: String) {
        updateContent { it.copy(email = email, emailError = null) }
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

    fun togglePasswordVisibility() {
        updateContent { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    private fun getPasswordError(password: String): Int? {
        if (password.isBlank()) return R.string.login_error_password_required
        if (password.length < 8) return R.string.error_password_too_short
        if (!password.any { it.isDigit() }) return R.string.error_password_no_digit
        if (!password.any { !it.isLetterOrDigit() }) return R.string.error_password_no_symbol
        return null
    }

    private fun resetErrors() {
        updateContent { it.copy(emailError = null, passwordError = null) }
    }

    fun login() {
        val state = _uiState.value as? LoginUiState.Content ?: return
        var hasError = false
        
        resetErrors()

        if (state.email.isBlank()) {
            updateContent { it.copy(emailError = R.string.login_error_email_required) }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            updateContent { it.copy(emailError = R.string.login_error_email_required) }
            hasError = true
        }

        val passError = getPasswordError(state.password)
        if (passError != null) {
            updateContent { it.copy(passwordError = passError) }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            updateContent { it.copy(isLoading = true, errorMessage = null) }
            try {
                loginUseCase(state.email, state.password)
                _uiState.value = LoginUiState.Success
            } catch (e: Exception) {
                updateContent { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun clearError() {
        updateContent { it.copy(errorMessage = null) }
    }
}
