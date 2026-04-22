package com.oracle.visualize.presentation.screens.selectchart

import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.VisualizationMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Chart Selection screen.
 * Handles the logic for selecting visualizations and editing their titles.
 * Aligned with MVVM and Clean Architecture.
 */
class ChartSelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ChartSelectionUiState>(ChartSelectionUiState.Loading)
    
    /**
     * The UI state of the screen, exposed as a [StateFlow].
     */
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    init {
        loadMockCharts()
    }

    /**
     * Loads the mock visualizations from the domain layer provider [VisualizationMockData].
     */
    private fun loadMockCharts() {
        // We obtain the mock data from our specialized domain model provider
        val mockCharts = VisualizationMockData.visualizations.map { VisualizationSelection(it) }
        _uiState.value = ChartSelectionUiState.Success(charts = mockCharts)
    }

    /**
     * Toggles the selection state of a specific visualization.
     * @param chartId The unique identifier of the visualization.
     */
    fun toggleSelection(chartId: String) {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            _uiState.update {
                currentState.copy(
                    charts = currentState.charts.map { 
                        if (it.visualization.id == chartId) it.copy(isSelected = !it.isSelected) 
                        else it 
                    }
                )
            }
        }
    }

    /**
     * Updates the title of a specific visualization.
     * @param chartId The unique identifier of the visualization.
     * @param newTitle The new title string.
     */
    fun updateChartTitle(chartId: String, newTitle: String) {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            _uiState.update {
                currentState.copy(
                    charts = currentState.charts.map { 
                        if (it.visualization.id == chartId) {
                            it.copy(visualization = it.visualization.copy(title = newTitle))
                        } else it 
                    }
                )
            }
        }
    }

    /**
     * Toggles the visibility of the unsaved changes dialog.
     * @param show True to show the dialog, false to hide it.
     */
    fun showUnsavedChangesDialog(show: Boolean) {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            _uiState.update { currentState.copy(isUnsavedChangesDialogVisible = show) }
        }
    }

    /**
     * Checks if at least one visualization is selected.
     * @return True if any chart is selected, false otherwise.
     */
    fun hasSelections(): Boolean {
        val currentState = _uiState.value
        return if (currentState is ChartSelectionUiState.Success) {
            currentState.charts.any { it.isSelected }
        } else {
            false
        }
    }
}
