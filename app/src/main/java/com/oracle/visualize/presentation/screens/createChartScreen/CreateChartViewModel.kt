package com.oracle.visualize.presentation.screens.createChartScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.models.SelectedDataset
import com.oracle.visualize.domain.usecases.ValidateDatasetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the Create Chart screen.
 * Follows the standard of calling Use Cases and exposing a single UI state.
 */
@HiltViewModel
class CreateChartViewModel @Inject constructor(
    private val validateDatasetUseCase: ValidateDatasetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateChartUiState())
    val uiState: StateFlow<CreateChartUiState> = _uiState.asStateFlow()

    /**
     * Handles file selection and starts validation/upload process.
     */
    fun onFileSelected(dataset: SelectedDataset) {
        val fileSizeFormatted = formatFileSize(dataset.sizeBytes)

        validateDatasetUseCase(dataset.name, dataset.sizeBytes).onSuccess {
            startUpload(dataset.name, fileSizeFormatted)
        }.onFailure { exception ->
            Log.e("CreateViewModel", "File validation failed: ${exception.message}")
            // Note: R.string.error_invalid_format would be better, but keeping consistency with existing logic
            _uiState.update { 
                it.copy(
                    isIdle = false,
                    error = com.oracle.visualize.R.string.error_invalid_format,
                    fileName = dataset.name,
                    fileSize = fileSizeFormatted
                )
            }
        }
    }

    /**
     * Simulates a file upload process.
     */
    private fun startUpload(fileName: String, fileSize: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isIdle = false,
                    isUploading = true,
                    fileName = fileName,
                    fileSize = fileSize,
                    uploadProgress = 0f
                ) 
            }
            
            for (progressValue in 1..100) {
                delay(15)
                _uiState.update { it.copy(uploadProgress = progressValue / 100f) }
            }
            
            _uiState.update { 
                it.copy(
                    isUploading = false,
                    isSuccess = true
                ) 
            }
        }
    }

    /**
     * Resets the screen state to idle.
     */
    fun resetState() {
        _uiState.value = CreateChartUiState()
    }

    /**
     * Formats bytes into a human-readable MB string.
     */
    private fun formatFileSize(sizeInBytes: Long): String {
        val mbSize = sizeInBytes / (1024f * 1024f)
        return String.format(Locale.ROOT, "%.1f MB", mbSize)
    }
}
