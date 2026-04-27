package com.oracle.visualize.presentation.screens.selectChartScreen

import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.ChartSelectionUiState
import com.oracle.visualize.domain.models.VisualizationSelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SelectChartViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ChartSelectionUiState>(ChartSelectionUiState.Loading)
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    init {
        loadMockCharts()
    }

    private fun loadMockCharts() {
        val mockCharts = selectChartMockData.visualizations.map { VisualizationSelection(it) }
        _uiState.value = ChartSelectionUiState.Success(charts = mockCharts)
    }

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

    fun showUnsavedChangesDialog(show: Boolean) {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            _uiState.update { currentState.copy(isUnsavedChangesDialogVisible = show) }
        }
    }

    fun hasSelections(): Boolean {
        val currentState = _uiState.value
        return if (currentState is ChartSelectionUiState.Success) {
            currentState.charts.any { it.isSelected }
        } else {
            false
        }
    }
}
