package com.oracle.visualize.domain.models

data class ChartSelectionUiState(
    val isLoading: Boolean = false,
    val charts: List<VisualizationSelection> = emptyList(),
    val errorMessage: String? = null,
    val isUnsavedChangesDialogVisible: Boolean = false
)

data class VisualizationSelection(
    val visualization: Visualization,
    val isSelected: Boolean = false
)
