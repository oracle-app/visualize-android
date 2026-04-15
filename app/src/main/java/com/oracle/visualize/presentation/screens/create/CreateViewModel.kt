package com.oracle.visualize.presentation.screens.create

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.GetDatasetInfoUseCase
import com.oracle.visualize.domain.usecases.ValidateDatasetUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Create Data Visualizations flow.
 * Coordinates between the View and the Domain layer via Use Cases.
 */
class CreateViewModel(
    private val getDatasetInfoUseCase: GetDatasetInfoUseCase,
    private val validateDatasetUseCase: ValidateDatasetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateUiState>(CreateUiState.Idle)
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    /**
     * Handles the file selection event.
     * Delegates to Use Cases for information retrieval and validation.
     */
    fun onFileSelected(uri: Uri?) {
        if (uri == null) return

        val dataset = getDatasetInfoUseCase(uri)
        if (dataset == null) {
            _uiState.value = CreateUiState.Error("Could not read file information.")
            return
        }

        validateDatasetUseCase(dataset.fileName).onSuccess {
            startUpload(dataset.fileName, dataset.fileSize)
        }.onFailure { exception ->
            _uiState.value = CreateUiState.Error(
                message = exception.message ?: "Unsupported format",
                fileName = dataset.fileName,
                fileSize = dataset.fileSize
            )
        }
    }

    private fun startUpload(fileName: String, fileSize: String) {
        viewModelScope.launch {
            _uiState.value = CreateUiState.Uploading(fileName, fileSize, 0f)
            
            // Simulating upload progress
            for (progressValue in 1..100) {
                delay(15) 
                if (_uiState.value is CreateUiState.Uploading) {
                    _uiState.value = CreateUiState.Uploading(fileName, fileSize, progressValue / 100f)
                }
            }
            
            _uiState.value = CreateUiState.Success(fileName, fileSize)
        }
    }

    /**
     * Resets the screen to its initial idle state.
     */
    fun resetState() {
        _uiState.value = CreateUiState.Idle
    }
}
