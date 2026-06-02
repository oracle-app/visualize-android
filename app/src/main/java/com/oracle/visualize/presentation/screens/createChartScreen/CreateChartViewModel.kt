package com.oracle.visualize.presentation.screens.createChartScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.SelectedDataset
import com.oracle.visualize.domain.repositories.AnalyzeRepository
import com.oracle.visualize.domain.usecases.visualization.ValidateDatasetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the Create Chart screen.
 * Handles file selection, validation, and simulated upload progress.
 *
 * @property validateDatasetUseCase Use case to validate the selected dataset.
 */
@HiltViewModel
class CreateChartViewModel @Inject constructor(
    private val validateDatasetUseCase: ValidateDatasetUseCase,
    private val repository: AnalyzeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateChartUiState>(CreateChartUiState.Idle)
    val uiState: StateFlow<CreateChartUiState> = _uiState.asStateFlow()

    private var uploadJob: Job? = null

    fun onFileSelected(dataset: SelectedDataset, file: File) {
        val fileSizeFormatted = formatFileSize(dataset.sizeBytes)

        when (val validationResult = validateDatasetUseCase(dataset.name, dataset.sizeBytes)) {
            is AppResult.Success -> {
                startUpload(dataset.name, fileSizeFormatted, file)
            }
            is AppResult.Error -> {
                Log.e("CreateViewModel", "File validation failed: ${validationResult.error.message}")
                _uiState.value = CreateChartUiState.Error(
                    message = R.string.error_invalid_format,
                    fileName = dataset.name,
                    fileSize = fileSizeFormatted
                )
            }

        }
    }

    private fun startUpload(fileName: String, fileSize: String, file: File) {
        uploadJob?.cancel()
        uploadJob = viewModelScope.launch {
            _uiState.value = CreateChartUiState.Uploading(fileName, fileSize, 0f)

            val animationJob = launch {
                for (progressValue in 1..90) {
                    delay(150)
                    if (_uiState.value is CreateChartUiState.Uploading) {
                        _uiState.value = CreateChartUiState.Uploading(fileName, fileSize, progressValue / 100f)
                    }
                }
            }

            when (val result = repository.analyzeData(file)) {
                is AppResult.Success -> {
                    animationJob.cancel()

                    _uiState.value = CreateChartUiState.Uploading(fileName, fileSize, 1f)
                    delay(400)

                    _uiState.value = CreateChartUiState.Success(fileName, fileSize, result.data)
                }
                is AppResult.Error -> {
                    animationJob.cancel()
                    Log.e("CreateViewModel", "Upload failed: ${result.error.message}")

                    val errorStringId = when (result.error) {
                        is AppError.NetworkError -> R.string.error_network
                        else -> R.string.error_generic
                    }
                    _uiState.value = CreateChartUiState.Error(
                        message = errorStringId,
                        fileName = fileName,
                        fileSize = fileSize
                    )
                }
            }
        }
    }

    fun resetState() {
        uploadJob?.cancel()
        uploadJob = null
        _uiState.value = CreateChartUiState.Idle
    }

    private fun formatFileSize(sizeInBytes: Long): String {
        val mbSize = sizeInBytes / (1024f * 1024f)
        return String.format(Locale.ROOT, "%.1f MB", mbSize)
    }
}
