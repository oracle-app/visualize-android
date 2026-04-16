package com.oracle.visualize.domain.models

/**
 * Represents the UI state for the Chart Selection screen.
 */
data class ChartSelectionUiState(
    val isLoading: Boolean = false,
    val charts: List<VisualizationSelection> = emptyList(),
    val errorMessage: String? = null,
    val isUnsavedChangesDialogVisible: Boolean = false
)

/**
 * Wrapper for Visualization with selection state.
 */
data class VisualizationSelection(
    val visualization: Visualization,
    val isSelected: Boolean = false
)
