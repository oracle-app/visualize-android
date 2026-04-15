package com.oracle.visualize.ui.screens.create

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class CreateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CreateUiState>(CreateUiState.Idle)
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    /**
     * Handles file selection and validates the extension.
     */
    fun onFileSelected(uri: Uri?, context: Context) {
        if (uri == null) return

        val fileName = getFileName(context, uri) ?: "unknown_file"
        val fileSize = getFileSize(context, uri) ?: "0 MB"
        val extension = fileName.substringAfterLast(".", "").lowercase(Locale.ROOT)

        if (extension != "csv" && extension != "xlsx") {
            _uiState.value = CreateUiState.Error(
                message = "Please upload a .xlsx or .csv file to continue.",
                fileName = fileName,
                fileSize = fileSize
            )
            return
        }

        startUpload(fileName, fileSize)
    }

    private fun startUpload(fileName: String, fileSize: String) {
        viewModelScope.launch {
            _uiState.value = CreateUiState.Uploading(fileName, fileSize, 0f)
            
            // Simulate upload progress
            for (progress in 1..100) {
                delay(20) 
                if (_uiState.value is CreateUiState.Uploading) {
                    _uiState.value = CreateUiState.Uploading(fileName, fileSize, progress / 100f)
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
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
        return name
    }

    private fun getFileSize(context: Context, uri: Uri): String? {
        var size: Long = 0
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.SIZE)
                if (index != -1) {
                    size = it.getLong(index)
                }
            }
        }
        val mbSize = size / (1024f * 1024f)
        return String.format(Locale.ROOT, "%.1f MB", mbSize)
    }
}
