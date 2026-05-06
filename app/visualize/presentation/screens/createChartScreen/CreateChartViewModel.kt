package com.oracle.visualize.presentation.screens.createChartScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
<<<<<<< Updated upstream
import com.oracle.visualize.domain.models.SelectedDataset
import com.oracle.visualize.domain.usecases.ValidateDatasetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
=======
import com.oracle.visualize.domain.usecases.ValidateDatasetUseCase
import com.oracle.visualize.presentation.screens.createChartScreen.CreateChartUiState
>>>>>>> Stashed changes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
<<<<<<< Updated upstream
import javax.inject.Inject

/**
 * ViewModel for the Create Chart screen.
 * Handles file selection, validation, and simulated upload progress.
 *
 * @property validateDatasetUseCase Use case to validate the selected dataset.
 */
@HiltViewModel
class CreateChartViewModel @Inject constructor(
    private val validateDatasetUseCase: ValidateDatasetUseCase
=======

class CreateChartViewModel(
    private val validateDatasetUseCase: ValidateDatasetUseCase = ValidateDatasetUseCase(),
>>>>>>> Stashed changes
) : ViewModel() {
    private val _uiState = MutableStateFlow<CreateChartUiState>(CreateChartUiState.Idle)
    val uiState: StateFlow<CreateChartUiState> = _uiState.asStateFlow()

<<<<<<< Updated upstream
    fun onFileSelected(dataset: SelectedDataset) {
        val fileSizeFormatted = formatFileSize(dataset.sizeBytes)

        validateDatasetUseCase(dataset.name, dataset.sizeBytes).onSuccess {
            startUpload(dataset.name, fileSizeFormatted)
        }.onFailure { exception ->
            Log.e("CreateViewModel", "File validation failed: ${exception.message}")
            _uiState.value = CreateChartUiState.Error(
                message = R.string.error_invalid_format,
                fileName = dataset.name,
                fileSize = fileSizeFormatted
            )
        }
=======
    fun onFileSelected(
        uri: Uri?,
        context: Context,
    ) {
        if (uri == null) return

        val fileName = getFileName(context, uri) ?: "unknown_file"
        val sizeInBytes = getFileSizeBytes(context, uri)
        val fileSizeFormatted = formatFileSize(sizeInBytes)

        validateDatasetUseCase(fileName, sizeInBytes)
            .onSuccess {
                startUpload(fileName, fileSizeFormatted)
            }.onFailure { exception ->
                Log.e("CreateViewModel", "File validation failed: ${exception.message}")
                _uiState.value =
                    CreateChartUiState.Error(
                        message = R.string.error_invalid_format,
                        fileName = fileName,
                        fileSize = fileSizeFormatted,
                    )
            }
>>>>>>> Stashed changes
    }

    private fun startUpload(
        fileName: String,
        fileSize: String,
    ) {
        viewModelScope.launch {
            _uiState.value = CreateChartUiState.Uploading(fileName, fileSize, 0f)
            for (progressValue in 1..100) {
                delay(15)
                if (_uiState.value is CreateChartUiState.Uploading) {
                    _uiState.value = CreateChartUiState.Uploading(fileName, fileSize, progressValue / 100f)
                }
            }
            _uiState.value = CreateChartUiState.Success(fileName, fileSize)
        }
    }

    fun resetState() {
        _uiState.value = CreateChartUiState.Idle
    }

<<<<<<< Updated upstream
=======
    private fun getFileName(
        context: Context,
        uri: Uri,
    ): String? {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = cursor.getString(index)
            }
        }
        return name
    }

    private fun getFileSizeBytes(
        context: Context,
        uri: Uri,
    ): Long {
        var size: Long = 0
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (index != -1) size = cursor.getLong(index)
            }
        }
        return size
    }

>>>>>>> Stashed changes
    private fun formatFileSize(sizeInBytes: Long): String {
        val mbSize = sizeInBytes / (1024f * 1024f)
        return String.format(Locale.ROOT, "%.1f MB", mbSize)
    }
}