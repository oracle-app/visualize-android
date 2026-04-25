package com.oracle.visualize.presentation.screens.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State for the Verification screen.
 */
data class VerificationUiState(
    val code: String = "",
    val codeError: Int? = null,
    val resendTimer: Int = 0,
    val isResendEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showResendWaitMessage: Boolean = false
)

@HiltViewModel
class VerificationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var waitMessageJob: Job? = null
    private val resendLimitSeconds = 30

    /**
     * Updates the code string, limiting it to 4 characters.
     */
    fun onCodeChange(newCode: String) {
        // Filter only numbers
        val filteredCode = newCode.filter { it.isDigit() }
        if (filteredCode.length <= 4) {
            _uiState.update { it.copy(
                code = filteredCode, 
                // Only clear the error if the user has typed something
                // or if it was the "required" error.
                codeError = if (filteredCode.isNotEmpty()) null else it.codeError 
            ) }
        }
    }

    /**
     * Handles resend code logic with a cooldown timer.
     */
    fun resendCode() {
        if (_uiState.value.resendTimer > 0) {
            showWaitAnnouncement()
            return
        }
        startTimer()
    }

    /**
     * Shows the "Please wait" message for 5 seconds.
     */
    private fun showWaitAnnouncement() {
        waitMessageJob?.cancel()
        _uiState.update { it.copy(showResendWaitMessage = true) }
        waitMessageJob = viewModelScope.launch {
            delay(5000)
            _uiState.update { it.copy(showResendWaitMessage = false) }
        }
    }

    /**
     * Starts the 30s countdown for resending the code.
     */
    private fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(resendTimer = resendLimitSeconds, isResendEnabled = false) }
        
        timerJob = viewModelScope.launch {
            while (_uiState.value.resendTimer > 0) {
                delay(1000)
                _uiState.update { it.copy(resendTimer = it.resendTimer - 1) }
            }
            _uiState.update { it.copy(isResendEnabled = true) }
        }
    }

    /**
     * Verifies the entered code.
     */
    fun verify() {
        val currentCode = _uiState.value.code
        
        if (currentCode.isBlank()) {
            _uiState.update { it.copy(codeError = com.oracle.visualize.R.string.verification_error_code_required) }
            return
        }

        if (currentCode.length < 4) {
            // If partial code, we can still say it's required/incomplete
            _uiState.update { it.copy(codeError = com.oracle.visualize.R.string.verification_error_code_required) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, codeError = null) }
            delay(1500) // Simulating network
            
            // Logic fixed: Use "Incorrect code" string when the code is wrong
            if (currentCode != "1234") { 
                _uiState.update { it.copy(
                    isLoading = false,
                    codeError = com.oracle.visualize.R.string.verification_error_incorrect_code
                ) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                // Success: proceed to next screen
            }
        }
    }
}
