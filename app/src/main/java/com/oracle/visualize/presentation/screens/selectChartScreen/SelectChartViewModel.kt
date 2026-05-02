package com.oracle.visualize.presentation.screens.selectChartScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Select Chart screen.
 * Manages the selection state and title editing of the suggested visualizations.
 */
@HiltViewModel
class SelectChartViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ChartSelectionUiState>(ChartSelectionUiState.Loading)
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    /** Tracks the original titles to detect unsaved changes. */
    private val originalTitles = mutableMapOf<String, String>()

    init {
        loadMockCharts()
    }

    private fun loadMockCharts() {
        val mockCharts = SelectChartMockData.visualizations.map { visualization ->
            originalTitles[visualization.id] = visualization.title
            VisualizationSelection(visualization)
        }
        _uiState.value = ChartSelectionUiState.Success(charts = mockCharts)
    }

    /** Toggles the selected state for the chart with the given [chartId]. */
    fun toggleSelection(chartId: String) {
        val currentState = _uiState.value as? ChartSelectionUiState.Success ?: return
        _uiState.update {
            currentState.copy(
                charts = currentState.charts.map {
                    if (it.visualization.id == chartId) it.copy(isSelected = !it.isSelected)
                    else it
                }
            )
        }
    }

    /**
     * Updates the display title of a chart and recalculates [ChartSelectionUiState.Success.hasTitleChanges]
     * by comparing against the original titles.
     */
    fun updateChartTitle(chartId: String, newTitle: String) {
        val currentState = _uiState.value as? ChartSelectionUiState.Success ?: return
        val updatedCharts = currentState.charts.map {
            if (it.visualization.id == chartId) it.copy(visualization = it.visualization.copy(title = newTitle))
            else it
        }
        val hasChanges = updatedCharts.any { originalTitles[it.visualization.id] != it.visualization.title }
        _uiState.update { currentState.copy(charts = updatedCharts, hasTitleChanges = hasChanges) }
    }

    /** Shows or hides the unsaved-changes confirmation dialog. */
    fun showUnsavedChangesDialog(show: Boolean) {
        val currentState = _uiState.value as? ChartSelectionUiState.Success ?: return
        _uiState.update { currentState.copy(isUnsavedChangesDialogVisible = show) }
    }

    /** Returns true when at least one chart card is selected. */
    fun hasSelections(): Boolean {
        val currentState = _uiState.value as? ChartSelectionUiState.Success ?: return false
        return currentState.charts.any { it.isSelected }
    }
}