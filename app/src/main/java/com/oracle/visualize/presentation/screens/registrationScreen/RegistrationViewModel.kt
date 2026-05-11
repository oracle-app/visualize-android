package com.oracle.visualize.presentation.screens.registrationScreen

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

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Content())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private fun updateContent(update: (RegistrationUiState.Content) -> RegistrationUiState) {
        _uiState.update { state ->
            if (state is RegistrationUiState.Content) update(state) else state
        }
    }

    fun onNameChange(name: String) {
        updateContent { it.copy(name = name, nameError = null) }
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

    fun onConfirmPasswordChange(confirmPassword: String) {
        updateContent { currentState ->
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
        updateContent { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        updateContent { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    private fun getPasswordError(password: String): Int? {
        if (password.isBlank()) return R.string.registration_error_password_required
        if (password.length < 8) return R.string.error_password_too_short
        if (!password.any { it.isDigit() }) return R.string.error_password_no_digit
        if (!password.any { !it.isLetterOrDigit() }) return R.string.error_password_no_symbol
        return null
    }

    fun validateInputs(): Boolean {
        val state = _uiState.value as? RegistrationUiState.Content ?: return false
        var hasError = false
        
        updateContent { it.copy(
            nameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null
        ) }

        if (state.name.isBlank()) {
            updateContent { it.copy(nameError = R.string.registration_error_name_required) }
            hasError = true
        }

        if (state.email.isBlank()) {
            updateContent { it.copy(emailError = R.string.registration_error_email_required) }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            updateContent { it.copy(emailError = R.string.registration_error_email_required) } 
            hasError = true
        }

        val passError = getPasswordError(state.password)
        if (passError != null) {
            updateContent { it.copy(passwordError = passError) }
            hasError = true
        }

        if (state.confirmPassword.isBlank()) {
            updateContent { it.copy(confirmPasswordError = R.string.registration_error_confirm_password_required) }
            hasError = true
        } else if (state.password != state.confirmPassword) {
            updateContent { it.copy(
                passwordError = R.string.registration_error_passwords_mismatch,
                confirmPasswordError = R.string.registration_error_passwords_mismatch
            ) }
            hasError = true
        }

        return !hasError
    }

    fun register() {
        if (!validateInputs()) return

        val state = _uiState.value as? RegistrationUiState.Content ?: return
        viewModelScope.launch {
            updateContent { it.copy(isLoading = true, errorMessage = null) }
            try {
                registerUseCase(state.name, state.email, state.password, state.confirmPassword)
                _uiState.value = RegistrationUiState.Success
            } catch (e: Exception) {
                updateContent { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun clearError() {
        updateContent { it.copy(errorMessage = null) }
    }
}
