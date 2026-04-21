package com.oracle.visualize.domain.models

/**
 * Represents the different states of the Create Data Visualizations screen.
 */
sealed interface CreateChartUiState {
    object Idle : CreateChartUiState
    
    data class Uploading(
        val fileName: String,
        val fileSize: String,
        val progress: Float
    ) : CreateChartUiState

    data class Success(
        val fileName: String,
        val fileSize: String
    ) : CreateChartUiState

    data class Error(
        val message: String,
        val fileName: String? = null,
        val fileSize: String? = null
    ) : CreateChartUiState
}
