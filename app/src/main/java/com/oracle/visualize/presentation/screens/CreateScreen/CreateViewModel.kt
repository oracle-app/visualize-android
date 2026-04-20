package com.oracle.visualize.presentation.screens.CreateScreen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.models.CreateUiState
import com.oracle.visualize.domain.usecases.ValidateDatasetUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class CreateViewModel(
    private val validateDatasetUseCase: ValidateDatasetUseCase = ValidateDatasetUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateUiState>(CreateUiState.Idle)
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri?, context: Context) {
        if (uri == null) return

        val fileName = getFileName(context, uri) ?: "unknown_file"
        val sizeInBytes = getFileSizeBytes(context, uri)
        val fileSizeFormatted = formatFileSize(sizeInBytes)

        validateDatasetUseCase(fileName, sizeInBytes).onSuccess {
            startUpload(fileName, fileSizeFormatted)
        }.onFailure { exception ->
            _uiState.value = CreateUiState.Error(
                message = exception.message ?: "Unsupported format",
                fileName = fileName,
                fileSize = fileSizeFormatted
            )
        }
    }

    private fun startUpload(fileName: String, fileSize: String) {
        viewModelScope.launch {
            _uiState.value = CreateUiState.Uploading(fileName, fileSize, 0f)
            for (progressValue in 1..100) {
                delay(15) 
                if (_uiState.value is CreateUiState.Uploading) {
                    _uiState.value = CreateUiState.Uploading(fileName, fileSize, progressValue / 100f)
                }
            }
            _uiState.value = CreateUiState.Success(fileName, fileSize)
        }
    }

    fun resetState() {
        _uiState.value = CreateUiState.Idle
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = cursor.getString(index)
            }
        }
        return name
    }

    private fun getFileSizeBytes(context: Context, uri: Uri): Long {
        var size: Long = 0
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (index != -1) size = cursor.getLong(index)
            }
        }
        return size
    }

    private fun formatFileSize(sizeInBytes: Long): String {
        val mbSize = sizeInBytes / (1024f * 1024f)
        return String.format(Locale.ROOT, "%.1f MB", mbSize)
    }
}
