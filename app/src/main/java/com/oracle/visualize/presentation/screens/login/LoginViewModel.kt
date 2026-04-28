package com.oracle.visualize.presentation.screens.login

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

/**
 * State for the Login screen.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val isPasswordVisible: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { currentState ->
            // If an error is already showing, we re-validate reactively.
            // If the password becomes correct, the error disappears.
            val nextError = if (currentState.passwordError != null) {
                getPasswordError(password)
            } else {
                null
            }
            currentState.copy(password = password, passwordError = nextError)
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    /**
     * Validates password complexity: 8 chars, 1 digit, 1 symbol.
     * Returns the string resource ID of the specific error or null if valid.
     */
    private fun getPasswordError(password: String): Int? {
        if (password.isBlank()) return R.string.login_error_password_required
        if (password.length < 8) return R.string.error_password_too_short
        if (!password.any { it.isDigit() }) return R.string.error_password_no_digit
        if (!password.any { !it.isLetterOrDigit() }) return R.string.error_password_no_symbol
        return null
    }

    /**
     * Resets any existing validation errors.
     */
    private fun resetErrors() {
        _uiState.update { it.copy(emailError = null, passwordError = null) }
    }

    fun login() {
        val state = _uiState.value
        var hasError = false
        
        resetErrors()

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = R.string.login_error_email_required) }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = R.string.login_error_email_required) }
            hasError = true
        }

        val passError = getPasswordError(state.password)
        if (passError != null) {
            _uiState.update { it.copy(passwordError = passError) }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                loginUseCase(state.email, state.password)
                _uiState.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
