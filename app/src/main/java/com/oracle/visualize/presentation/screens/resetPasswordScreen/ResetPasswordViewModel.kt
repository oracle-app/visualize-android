package com.oracle.visualize.presentation.screens.resetPasswordScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.usecases.ResetPasswordUseCase
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
                emailError = null,
                error = null
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
                    emailError = null,
                    error = null,
                    success = false
                )
            }

            val result = resetPasswordUseCase(currentState.email)

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
                    val message = exception.message ?: "Unable to send reset link. Please try again."

                    when (exception) {
                        is AppError.AuthValidationError -> {
                            if (exception.field == AppError.AuthField.EMAIL) {
                                _uiState.update {
                                    it.copy(isLoading = false, emailError = message)
                                }
                            } else {
                                _uiState.update {
                                    it.copy(isLoading = false, error = message)
                                }
                            }
                        }
                        is AppError.NetworkError -> {
                            _uiState.update {
                                it.copy(isLoading = false, error = message)
                            }
                        }
                        else -> {
                            _uiState.update {
                                it.copy(isLoading = false, error = message)
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
