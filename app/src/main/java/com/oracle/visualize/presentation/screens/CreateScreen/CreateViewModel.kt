package com.oracle.visualize.presentation.screens.CreateScreen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
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
        val fileSize = getFileSize(context, uri) ?: "0 MB"

        validateDatasetUseCase(fileName).onSuccess {
            startUpload(fileName, fileSize)
        }.onFailure { exception ->
            Log.e("CreateViewModel", "File validation failed: ${exception.message}")
            _uiState.value = CreateUiState.Error(
                message = R.string.error_invalid_format,
                fileName = fileName,
                fileSize = fileSize
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

    private fun getFileSize(context: Context, uri: Uri): String? {
        var size: Long = 0
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (index != -1) size = cursor.getLong(index)
            }
        }
        val mbSize = size / (1024f * 1024f)
        return String.format(Locale.ROOT, "%.1f MB", mbSize)
    }
}
