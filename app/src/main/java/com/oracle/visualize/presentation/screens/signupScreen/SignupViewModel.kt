package com.oracle.visualize.presentation.screens.signupScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.usecases.RegisterUseCase
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
            it.copy(name = name, nameError = null, error = null)
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, emailError = null, error = null)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password, passwordError = null, error = null)
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = null,
                error = null
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
                    nameError = null,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    error = null,
                    success = false
                )
            }

            val result = registerUseCase(
                name = state.name,
                email = state.email,
                password = state.password,
                confirmPassword = state.confirmPassword
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = true
                        )
                    }
                },
                onFailure = { exception ->
                    val message = exception.message ?: "Unable to create account. Please try again."

                    if (exception is AppError.ValidationError) {
                        when {
                            message.contains("Name", ignoreCase = true) -> {
                                _uiState.update {
                                    it.copy(isLoading = false, nameError = message)
                                }
                            }

                            message.contains("Email", ignoreCase = true) -> {
                                _uiState.update {
                                    it.copy(isLoading = false, emailError = message)
                                }
                            }

                            message.contains("Confirm", ignoreCase = true) ||
                                message.contains("mismatch", ignoreCase = true) -> {
                                _uiState.update {
                                    it.copy(isLoading = false, confirmPasswordError = message)
                                }
                            }

                            message.contains("Password", ignoreCase = true) -> {
                                _uiState.update {
                                    it.copy(isLoading = false, passwordError = message)
                                }
                            }

                            else -> {
                                _uiState.update {
                                    it.copy(isLoading = false, error = message)
                                }
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = message
                            )
                        }
                    }
                }
            )
        }
    }
}
