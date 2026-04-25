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
            _uiState.update { it.copy(nameError = com.oracle.visualize.R.string.registration_error_name_required) }
            hasError = true
        }

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = com.oracle.visualize.R.string.registration_error_email_required) }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = com.oracle.visualize.R.string.registration_error_email_required) } 
            hasError = true
        }

        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = com.oracle.visualize.R.string.registration_error_password_required) }
            hasError = true
        }

        if (state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(confirmPasswordError = com.oracle.visualize.R.string.registration_error_confirm_password_required) }
            hasError = true
        } else if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(
                passwordError = com.oracle.visualize.R.string.registration_error_passwords_mismatch,
                confirmPasswordError = com.oracle.visualize.R.string.registration_error_passwords_mismatch
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