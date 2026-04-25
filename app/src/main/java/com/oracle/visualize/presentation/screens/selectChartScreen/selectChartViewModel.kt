package com.oracle.visualize.presentation.screens.selectchartscreen

import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.ChartSelectionUiState
import com.oracle.visualize.domain.models.VisualizationSelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Chart Selection screen.
 * Manages the state of suggested charts and user selections.
 */
class SelectChartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ChartSelectionUiState>(ChartSelectionUiState.Loading)
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    init {
        loadSuggestedCharts()
    }

    private fun loadSuggestedCharts() {
        // Load data from mock source for now
        val mockCharts = SelectChartMockData.visualizations.map { VisualizationSelection(it) }
        _uiState.value = ChartSelectionUiState.Success(charts = mockCharts)
    }

    /**
     * Toggles the selection status of a chart.
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
     * Updates the title of a specific chart.
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
     * Controls the visibility of the unsaved changes warning.
     */
    fun showUnsavedChangesDialog(show: Boolean) {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            _uiState.update { currentState.copy(isUnsavedChangesDialogVisible = show) }
        }
    }

    /**
     * Checks if at least one chart is selected for publishing.
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
