package com.oracle.visualize.presentation.screens.createschart

/**
 * CreateChartUiState defines the possible states for the dataset upload
 * and chart generation process.
 * This is located in the presentation layer as it represents pure UI states.
 */
sealed interface CreateChartUiState {
    /**
     * Initial state where no file has been selected yet.
     */
    object Idle : CreateChartUiState
    
    /**
     * State representing the file upload or processing progress.
     * @property fileName Name of the file being processed.
     * @property fileSize Formatted size string of the file.
     * @property progress Progress percentage from 0.0 to 1.0.
     */
    data class Uploading(
        val fileName: String,
        val fileSize: String,
        val progress: Float
    ) : CreateChartUiState

    /**
     * State representing a successful dataset processing.
     */
    data class Success(
        val fileName: String,
        val fileSize: String
    ) : CreateChartUiState

    /**
     * State representing an error occurred during the process.
     * @property message The descriptive error message.
     */
    data class Error(
        val message: String,
        val fileName: String? = null,
        val fileSize: String? = null
    ) : CreateChartUiState
}
