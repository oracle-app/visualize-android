package com.oracle.visualize.presentation.screens.selectchart

import com.oracle.visualize.domain.models.Visualization

/**
 * ChartSelectionUiState defines the various states of the Chart Selection screen.
 * It is located in the presentation layer as it handles UI-specific data 
 * representation.
 */
sealed interface ChartSelectionUiState {
    /**
     * Represents the state where data is being fetched or prepared.
     */
    object Loading : ChartSelectionUiState
    
    /**
     * Represents a successful data load, containing the list of available charts.
     * @param charts The list of visualizations with their selection status.
     * @param isUnsavedChangesDialogVisible Flag to toggle the 'Unsaved Changes' alert.
     */
    data class Success(
        val charts: List<VisualizationSelection> = emptyList(),
        val isUnsavedChangesDialogVisible: Boolean = false
    ) : ChartSelectionUiState

    /**
     * Represents an error state with a descriptive message.
     */
    data class Error(val message: String) : ChartSelectionUiState
}

/**
 * Wrapper for [Visualization] that adds a selection state for the UI.
 */
data class VisualizationSelection(
    val visualization: Visualization,
    val isSelected: Boolean = false
)
