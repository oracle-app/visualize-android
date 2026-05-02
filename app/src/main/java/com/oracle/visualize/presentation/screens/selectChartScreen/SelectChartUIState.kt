package com.oracle.visualize.presentation.screens.selectChartScreen

import com.oracle.visualize.domain.models.Visualization

/**
 * Represents the UI state for the Chart Selection screen.
 */
sealed interface ChartSelectionUiState {

    object Loading : ChartSelectionUiState

    data class Success(
        val charts: List<VisualizationSelection> = emptyList(),
        val isUnsavedChangesDialogVisible: Boolean = false,
        val hasTitleChanges: Boolean = false
    ) : ChartSelectionUiState

    data class Error(val message: String) : ChartSelectionUiState
}

/**
 * Wrapper for a Visualization pairing it with its current selection state.
 */
data class VisualizationSelection(
    val visualization: Visualization,
    val isSelected: Boolean = false
)