package com.oracle.visualize.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class LoginViewModel(private val login: LoginUseCase): ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String){
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try{
                login(email, password)
                _uiState.value = LoginUiState(success = true)
            } catch (e: Exception){
                _uiState.value = LoginUiState(error = e.message)
            }
        }
    }
}