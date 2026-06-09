package com.oracle.visualize.presentation.screens.signupScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.usecases.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(name = name, nameErrorRes = null, errorRes = null)
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, emailErrorRes = null, errorRes = null)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password, passwordErrorRes = null, errorRes = null)
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordErrorRes = null,
                errorRes = null
            )
        }
    }

    fun onPasswordVisibilityChange() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun onConfirmPasswordVisibilityChange() {
        _uiState.update {
            it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible)
        }
    }

    fun signUp() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    nameErrorRes = null,
                    emailErrorRes = null,
                    passwordErrorRes = null,
                    confirmPasswordErrorRes = null,
                    errorRes = null,
                    success = false
                )
            }

            val result = registerUseCase(
                name = state.name,
                email = state.email,
                password = state.password,
                confirmPassword = state.confirmPassword
            )

            when (result) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = true
                        )
                    }
                }
                is AppResult.Error -> {
                    val exception = result.error

                    when (exception) {
                        is AppError.AuthValidationError -> {

                            when (exception.field) {
                                AppError.AuthField.NAME -> {
                                    val msg = exception.message ?: ""

                                    val resId = when {

                                        // Empty Name
                                        msg.contains("Required", ignoreCase = true) -> {
                                            R.string.error_auth_field_empty
                                        }

                                        // Short Name
                                        else -> {
                                            R.string.error_name_length
                                        }
                                    }
                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            nameErrorRes = resId
                                        )
                                    }
                                }
                                AppError.AuthField.EMAIL -> {
                                    val msg = exception.message ?: ""

                                    val resId = when {

                                        // Empty Email
                                        msg.contains("Required", ignoreCase = true) -> {
                                            R.string.error_auth_field_empty
                                        }

                                        // Invalid Email
                                        else -> {
                                            R.string.error_email_invalid
                                        }
                                    }
                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            emailErrorRes = resId
                                        )
                                    }
                                }
                                AppError.AuthField.PASSWORD -> {
                                    val msg = exception.message ?: ""

                                    val resId = when {

                                        // Empty Password
                                        msg.contains("Required", ignoreCase = true) -> {
                                            R.string.error_auth_field_empty
                                        }

                                        // Short Password
                                        msg.contains("12", ignoreCase = true) -> {
                                            R.string.error_password_length
                                        }

                                        // Special Character
                                        msg.contains("special", ignoreCase = true) -> {
                                            R.string.error_password_special
                                        }

                                        // Weak Password
                                        else -> {
                                            R.string.error_password_weak
                                        }
                                    }

                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            passwordErrorRes = resId
                                        )
                                    }
                                }
                                AppError.AuthField.CONFIRM_PASSWORD -> {
                                    val msg = exception.message ?: ""

                                    val resId = when {

                                        // Empty Confirm Password
                                        msg.contains("Required", ignoreCase = true) -> {
                                            R.string.error_auth_field_empty
                                        }

                                        // Passwords Mismatches
                                        else -> {
                                            R.string.error_passwords_not_match
                                        }
                                    }

                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            confirmPasswordErrorRes = resId
                                        )
                                    }
                                }
                            }
                        }

                        is AppError.EmailAlreadyExists -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    emailErrorRes = R.string.error_email_already
                                )

                            }
                        }

                        is AppError.NetworkError -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorRes = R.string.error_network
                                )
                            }
                        }

                        else -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorRes = R.string.error_alert
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}
