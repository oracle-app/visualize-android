package com.oracle.visualize.domain.models

import androidx.annotation.StringRes

/**
 * Represents the different states of the Create Data Visualizations screen.
 */
sealed interface CreateUiState {
    object Idle : CreateUiState
    
    data class Uploading(
        val fileName: String,
        val fileSize: String,
        val progress: Float
    ) : CreateUiState

    data class Success(
        val fileName: String,
        val fileSize: String
    ) : CreateUiState

    data class Error(
        @StringRes val message: Int,
        val fileName: String? = null,
        val fileSize: String? = null
    ) : CreateUiState
}
