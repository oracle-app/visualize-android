package com.oracle.visualize.presentation.screens.loginScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

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

    fun mapError(raw: String?): String = when {
        raw == null                     -> "An unexpected error occurred."
        raw.contains("password")        -> "Incorrect password. Please try again."
        raw.contains("no user record")  -> "No account found with this email."
        raw.contains("badly formatted") -> "Please enter a valid email address."
        raw.contains("blocked")         -> "Too many attempts. Try again later."
        raw.contains("network")         -> "No internet connection."
        else                            -> "Login failed. Please check your credentials."
    }

}