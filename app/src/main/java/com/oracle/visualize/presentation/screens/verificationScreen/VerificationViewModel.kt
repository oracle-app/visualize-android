package com.oracle.visualize.presentation.screens.verificationScreen

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
import com.oracle.visualize.R

@HiltViewModel
class VerificationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<VerificationUiState>(VerificationUiState.Content())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var waitMessageJob: Job? = null
    private val resendLimitSeconds = 30

    private fun updateContent(update: (VerificationUiState.Content) -> VerificationUiState) {
        _uiState.update { state ->
            if (state is VerificationUiState.Content) update(state) else state
        }
    }

    fun onCodeChange(newCode: String) {
        val filteredCode = newCode.filter { it.isDigit() }
        if (filteredCode.length <= 4) {
            updateContent { it.copy(
                code = filteredCode, 
                codeError = if (filteredCode.isNotEmpty()) null else it.codeError 
            ) }
        }
    }

    fun resendCode() {
        val state = _uiState.value as? VerificationUiState.Content ?: return
        if (state.resendTimer > 0) {
            showWaitAnnouncement()
            return
        }
        startTimer()
    }

    private fun showWaitAnnouncement() {
        waitMessageJob?.cancel()
        updateContent { it.copy(showResendWaitMessage = true) }
        waitMessageJob = viewModelScope.launch {
            delay(5000)
            updateContent { it.copy(showResendWaitMessage = false) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        updateContent { it.copy(resendTimer = resendLimitSeconds, isResendEnabled = false) }
        
        timerJob = viewModelScope.launch {
            while (true) {
                val state = _uiState.value as? VerificationUiState.Content ?: break
                if (state.resendTimer <= 0) break
                delay(1000)
                updateContent { it.copy(resendTimer = it.resendTimer - 1) }
            }
            updateContent { it.copy(isResendEnabled = true) }
        }
    }

    fun verify() {
        val state = _uiState.value as? VerificationUiState.Content ?: return
        val currentCode = state.code
        
        if (currentCode.isBlank()) {
            updateContent { it.copy(codeError = R.string.verification_error_code_required) }
            return
        }

        if (currentCode.length < 4) {
            updateContent { it.copy(codeError = R.string.verification_error_code_required) }
            return
        }

        viewModelScope.launch {
            updateContent { it.copy(isLoading = true, codeError = null) }
            delay(1500) 
            
            if (currentCode != "1234") { 
                updateContent { it.copy(
                    isLoading = false,
                    codeError = R.string.verification_error_incorrect_code
                ) }
            } else {
                _uiState.value = VerificationUiState.Success
            }
        }
    }
}
