package com.oracle.visualize.presentation.screens.loginScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.usecases.auth.LoginUseCase
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
                emailErrorRes = null,
                errorRes = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordErrorRes = null,
                errorRes = null
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
                    emailErrorRes = null,
                    passwordErrorRes = null,
                    errorRes = null,
                    success = false
                )
            }

            val result = loginUseCase(
                currentState.email,
                currentState.password
            )

            when (result) {
                is AppResult.Success<*> -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = true
                        )
                    }
                }
                is AppResult.Error -> {
                    when (val exception = result.error) {
                        is AppError.AuthValidationError -> {

                            when (exception.field) {
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
                                    _uiState.update {
                                        it.copy(
                                            isLoading = false,
                                            passwordErrorRes = R.string.error_auth_field_empty
                                        )
                                    }
                                }

                                else -> {}
                            }
                        }
                        is AppError.InvalidCredentials -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorRes = R.string.error_invalid_credentials
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
                                    errorRes = R.string.error_unknown_retry
                                )
                            }
                        }
                    }

                }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorRes = null)
        }
    }
}
