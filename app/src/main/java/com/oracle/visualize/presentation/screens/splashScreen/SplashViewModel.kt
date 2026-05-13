package com.oracle.visualize.presentation.screens.splashScreen

import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.usecases.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUIState())
    val uiState: StateFlow<SplashUIState> = _uiState.asStateFlow()

    fun checkSession() {
        val currentUser = getCurrentUserUseCase()

        _uiState.value = SplashUIState(
            isCheckingSession = false,
            hasActiveSession = currentUser != null
        )
    }
}
