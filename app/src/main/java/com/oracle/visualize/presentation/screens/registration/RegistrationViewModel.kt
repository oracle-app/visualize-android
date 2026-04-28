package com.oracle.visualize.presentation.screens.registration

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.oracle.visualize.R

data class RegistrationUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { currentState ->
            val nextError = if (currentState.passwordError != null) {
                getPasswordError(password)
            } else {
                null
            }
            currentState.copy(password = password, passwordError = nextError)
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { currentState ->
            val nextError = if (currentState.confirmPasswordError != null) {
                if (confirmPassword.isEmpty()) R.string.registration_error_confirm_password_required
                else if (currentState.password != confirmPassword) R.string.registration_error_passwords_mismatch
                else null
            } else {
                null
            }
            currentState.copy(confirmPassword = confirmPassword, confirmPasswordError = nextError)
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    /**
     * Validates password complexity: 8 chars, 1 digit, 1 symbol.
     * Returns the string resource ID of the specific error or null if valid.
     */
    private fun getPasswordError(password: String): Int? {
        if (password.isBlank()) return R.string.registration_error_password_required
        if (password.length < 8) return R.string.error_password_too_short
        if (!password.any { it.isDigit() }) return R.string.error_password_no_digit
        if (!password.any { !it.isLetterOrDigit() }) return R.string.error_password_no_symbol
        return null
    }

    fun validateInputs(): Boolean {
        val state = _uiState.value
        var hasError = false
        
        _uiState.update { it.copy(
            nameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null
        ) }

        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = R.string.registration_error_name_required) }
            hasError = true
        }

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = R.string.registration_error_email_required) }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = R.string.registration_error_email_required) } 
            hasError = true
        }

        val passError = getPasswordError(state.password)
        if (passError != null) {
            _uiState.update { it.copy(passwordError = passError) }
            hasError = true
        }

        if (state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(confirmPasswordError = R.string.registration_error_confirm_password_required) }
            hasError = true
        } else if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(
                passwordError = R.string.registration_error_passwords_mismatch,
                confirmPasswordError = R.string.registration_error_passwords_mismatch
            ) }
            hasError = true
        }

        return !hasError
    }

    fun register() {
        if (!validateInputs()) return

        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                registerUseCase(state.email, state.password)
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
