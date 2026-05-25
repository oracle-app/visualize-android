package com.oracle.visualize.presentation.screens.selectChartScreen
import com.oracle.visualize.domain.models.Chart
import com.oracle.visualize.domain.models.Visualization
import java.util.UUID
/**
 * Represents the UI state for the Chart Selection screen.
 */
sealed interface ChartSelectionUiState {
    object Loading : ChartSelectionUiState
    
    data class Success(
        val charts: List<ChartSelection> = emptyList(),
        val isUnsavedChangesDialogVisible: Boolean = false,
        val hasTitleChanges: Boolean = false
    ) : ChartSelectionUiState

    data class Error(val message: String) : ChartSelectionUiState
}

/**
 * Wrapper for Visualization with selection state.
 */
data class ChartSelection(
    val id: String = UUID.randomUUID().toString(),
    val chartIndex: Int,
    val chart: Chart<*>,
    val customTitle: String,
    val isSelected: Boolean = false
)
