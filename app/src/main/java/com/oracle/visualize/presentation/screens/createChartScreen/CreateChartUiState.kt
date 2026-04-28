package com.oracle.visualize.presentation.screens.createChartScreen

/**
 * Represents the UI state for the Create Chart screen.
 */
data class CreateChartUiState(
    val isIdle: Boolean = true,
    val isUploading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: Int? = null,
    val fileName: String? = null,
    val fileSize: String? = null,
    val uploadProgress: Float = 0f
)
