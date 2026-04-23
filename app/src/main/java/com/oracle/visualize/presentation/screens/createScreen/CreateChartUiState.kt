package com.oracle.visualize.presentation.screens.createScreen

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
        val message: Int,
        val fileName: String? = null,
        val fileSize: String? = null
    ) : CreateChartUiState
}
