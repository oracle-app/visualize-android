package com.oracle.visualize.presentation.screens.loginScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Login screen.
 * Manages the UI state and orchestrates the login process using [LoginUseCase].
 *
 * @property loginUseCase The use case for performing the login operation.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
                error = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                error = null
            )
        }
    }

    fun onPasswordVisibilityChange() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun login() {
        val currentState = _uiState.value

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    emailError = null,
                    passwordError = null,
                    error = null,
                    success = false
                )
            }

            val result = loginUseCase(
                currentState.email,
                currentState.password
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
                    when {
                        exception is AppError.ValidationError &&
                            exception.message?.contains("Email") == true -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    emailError = exception.message
                                )
                            }
                        }

                        exception is AppError.ValidationError &&
                            exception.message?.contains("Password") == true -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    passwordError = exception.message
                                )
                            }
                        }

                        else -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = exception.message ?: "Incorrect email or password. Please try again."
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}
