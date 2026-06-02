package com.oracle.visualize.presentation.screens.resetPasswordScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.usecases.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Reset Password screen.
 * Manages the UI state and orchestrates the process using [ResetPasswordUseCase].
 */
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailErrorRes = null,
                errorRes = null
            )
        }
    }

    fun resetPassword() {
        val currentState = _uiState.value

        if (currentState.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    emailErrorRes = null,
                    errorRes = null,
                    success = false
                )
            }

            when (val result = resetPasswordUseCase(currentState.email)) {
                is AppResult.Success -> {
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

                                else -> {}
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
