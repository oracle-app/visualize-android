package com.oracle.visualize.presentation.screens.registerScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserCase: RegisterUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onRegister(name: String, email: String, password: String, confirmPassword: String){
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            registerUserCase(name, email, password, confirmPassword).fold(
                onSuccess = { _uiState.value = RegisterUiState.Success},
                onFailure = { error ->
                    _uiState.value = RegisterUiState.Error(error.message ?: "Unknown failed")}
            )
        }

    }


}