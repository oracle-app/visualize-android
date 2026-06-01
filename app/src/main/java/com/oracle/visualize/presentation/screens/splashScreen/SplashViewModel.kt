package com.oracle.visualize.presentation.screens.splashScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.auth.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUIState())
    val uiState: StateFlow<SplashUIState> = _uiState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase()

            // Elegant delay to show the clean branding splash screen
            delay(800)

            _uiState.value = SplashUIState(
                isCheckingSession = false,
                hasActiveSession = currentUser != null
            )
        }
    }
}
