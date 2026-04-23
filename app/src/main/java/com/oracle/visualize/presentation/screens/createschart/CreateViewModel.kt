package com.oracle.visualize.presentation.screens.createschart

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CreateChartUiState>(CreateChartUiState.Idle)
    val uiState: StateFlow<CreateChartUiState> = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri?, context: Context) {
        if (uri == null) return

        val fileName = uri.lastPathSegment ?: "dataset.csv"
        
        viewModelScope.launch {
            _uiState.value = CreateChartUiState.Uploading(
                fileName = fileName,
                fileSize = "1.2 MB",
                progress = 0.0f
            )

            // Simulate upload progress
            for (i in 1..10) {
                delay(200)
                val currentProgress = i / 10f
                _uiState.value = CreateChartUiState.Uploading(
                    fileName = fileName,
                    fileSize = "1.2 MB",
                    progress = currentProgress
                )
            }

            _uiState.value = CreateChartUiState.Success(
                fileName = fileName,
                fileSize = "1.2 MB"
            )
        }
    }

    fun resetState() {
        _uiState.value = CreateChartUiState.Idle
    }
}
