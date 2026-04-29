package com.oracle.visualize.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state for the Login screen.
 *
 * @property isLoading Indicates if a login operation is in progress.
 * @property error Contains an error message if the login fails.
 * @property success Indicates if the login was successful.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

/**
 * ViewModel for the Login screen.
 * Manages the UI state and orchestrates the login process using [LoginUseCase].
 *
 * @property loginUseCase The use case for performing the login operation.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase): ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String){
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try{
                loginUseCase(email, password)
                _uiState.value = LoginUiState(success = true)
            } catch (e: Exception){
                _uiState.value = LoginUiState(error = e.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}