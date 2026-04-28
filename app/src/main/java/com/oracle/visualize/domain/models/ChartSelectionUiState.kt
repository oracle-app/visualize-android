package com.oracle.visualize.domain.models

/**
 * Represents the UI state for the Chart Selection screen.
 */
sealed interface ChartSelectionUiState {
    
    data object Loading : ChartSelectionUiState
    
    data class Success(
        val charts: List<VisualizationSelection> = emptyList(),
        val isUnsavedChangesDialogVisible: Boolean = false,
        val editingChartId: String? = null,
        val editingTitle: String = ""
    ) : ChartSelectionUiState

    data class Error(val message: String) : ChartSelectionUiState

    /**
     * Wrapper for Visualization with selection state.
     */
    data class VisualizationSelection(
        val visualization: Visualization,
        val isSelected: Boolean = false
    )
}
