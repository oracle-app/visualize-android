package com.oracle.visualize.ui.screens.chartselection

import androidx.lifecycle.ViewModel
import com.oracle.visualize.data.model.Chart
import com.oracle.visualize.data.repository.ChartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI State for the Chart Selection Screen.
 */
data class ChartSelectionUiState(
    val charts: List<Chart> = emptyList(),
    val isLoading: Boolean = false,
    val isEditDialogVisible: Boolean = false,
    val editingChart: Chart? = null
)

/**
 * ViewModel responsible for managing the state of the Chart Selection screen.
 */
class ChartSelectionViewModel(
    private val repository: ChartRepository = ChartRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartSelectionUiState())
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    init {
        loadCharts()
    }

    /**
     * Loads the available charts from the repository.
     */
    private fun loadCharts() {
        _uiState.update { it.copy(isLoading = true) }
        val availableCharts = repository.getCharts()
        _uiState.update {
            it.copy(
                charts = availableCharts,
                isLoading = false
            )
        }
    }

    /**
     * Toggles the selection state of a specific chart.
     */
    fun toggleSelection(chartId: String) {
        _uiState.update { currentState ->
            val updatedCharts = currentState.charts.map { chart ->
                if (chart.id == chartId) {
                    chart.copy(isSelected = !chart.isSelected)
                } else {
                    chart
                }
            }
            currentState.copy(charts = updatedCharts)
        }
    }

    /**
     * Prepares the UI to edit a chart's title.
     */
    fun onEditClick(chart: Chart) {
        _uiState.update {
            it.copy(
                isEditDialogVisible = true,
                editingChart = chart
            )
        }
    }

    /**
     * Dismisses the edit title dialog.
     */
    fun onDismissEditDialog() {
        _uiState.update {
            it.copy(
                isEditDialogVisible = false,
                editingChart = null
            )
        }
    }

    /**
     * Updates the title of the currently editing chart.
     */
    fun onUpdateChartTitle(newTitle: String) {
        _uiState.update { currentState ->
            val updatedCharts = currentState.charts.map { chart ->
                if (chart.id == currentState.editingChart?.id) {
                    chart.copy(title = newTitle)
                } else {
                    chart
                }
            }
            currentState.copy(
                charts = updatedCharts,
                isEditDialogVisible = false,
                editingChart = null
            )
        }
    }
}
