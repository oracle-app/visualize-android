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
 * Manages the selection of visualizations from a list.
 */
@HiltViewModel
class SelectChartViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ChartSelectionUiState>(ChartSelectionUiState.Loading)
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    init {
        loadMockCharts()
    }

    private fun loadMockCharts() {
        val mockCharts = SelectChartMockData.visualizations.map { VisualizationSelection(it) }
        _uiState.value = ChartSelectionUiState.Success(charts = mockCharts)
    }

    fun toggleSelection(chartId: String) {
        _uiState.update { currentState ->
            if (currentState is ChartSelectionUiState.Success) {
                currentState.copy(
                    charts = currentState.charts.map { 
                        if (it.visualization.id == chartId) it.copy(isSelected = !it.isSelected) 
                        else it 
                    }
                )
            } else currentState
        }
    }

    fun updateChartTitle(chartId: String, newTitle: String) {
        _uiState.update { currentState ->
            if (currentState is ChartSelectionUiState.Success) {
                currentState.copy(
                    hasTitleChanges = true,
                    charts = currentState.charts.map { 
                        if (it.visualization.id == chartId) {
                            it.copy(visualization = it.visualization.copy(title = newTitle))
                        } else it 
                    }
                )
            } else currentState
        }
    }

    fun showUnsavedChangesDialog(show: Boolean) {
        _uiState.update { currentState ->
            if (currentState is ChartSelectionUiState.Success) {
                currentState.copy(isUnsavedChangesDialogVisible = show)
            } else currentState
        }
    }

    fun hasSelections(): Boolean {
        val state = _uiState.value
        return if (state is ChartSelectionUiState.Success) {
            state.charts.any { it.isSelected }
        } else false
    }
}
