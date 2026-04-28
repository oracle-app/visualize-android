package com.oracle.visualize.presentation.screens.selectchartscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.models.ChartSelectionUiState
import com.oracle.visualize.domain.usecases.GetSuggestedChartsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Chart Selection screen.
 * Manages the state of suggested charts and coordinates user interactions
 * like selection and title editing before publishing.
 */
@HiltViewModel
class SelectChartViewModel @Inject constructor(
    private val getSuggestedChartsUseCase: GetSuggestedChartsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChartSelectionUiState>(ChartSelectionUiState.Loading)
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    init {
        loadSuggestedCharts()
    }

    /**
     * Triggers the use case to fetch suggested visualizations based on the uploaded data.
     * Updates the UI state to Success or Error based on the result.
     */
    private fun loadSuggestedCharts() {
        viewModelScope.launch {
            _uiState.value = ChartSelectionUiState.Loading
            try {
                val suggestedCharts = getSuggestedChartsUseCase()
                val selections = suggestedCharts.map { 
                    ChartSelectionUiState.VisualizationSelection(it) 
                }
                _uiState.value = ChartSelectionUiState.Success(charts = selections)
            } catch (e: Exception) {
                _uiState.value = ChartSelectionUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    /**
     * Toggles the selection status (checked/unchecked) of a specific chart card.
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
     * Prepares the state to start editing a chart title by opening the edit dialog.
     */
    fun startEditingTitle(chartId: String, currentTitle: String) {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            _uiState.update { 
                currentState.copy(editingChartId = chartId, editingTitle = currentTitle) 
            }
        }
    }

    /**
     * Updates the temporary title state while the user is typing in the dialog.
     */
    fun onEditingTitleChanged(newTitle: String) {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            _uiState.update { currentState.copy(editingTitle = newTitle) }
        }
    }

    /**
     * Applies the edited title to the selected visualization and closes the dialog.
     */
    fun confirmTitleEdit() {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            val updatedCharts = currentState.charts.map { 
                if (it.visualization.id == currentState.editingChartId) {
                    it.copy(visualization = it.visualization.copy(title = currentState.editingTitle))
                } else it 
            }
            _uiState.value = currentState.copy(
                charts = updatedCharts,
                editingChartId = null,
                editingTitle = ""
            )
        }
    }

    /**
     * Discards any title changes and closes the editing dialog.
     */
    fun cancelTitleEdit() {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            _uiState.update { currentState.copy(editingChartId = null, editingTitle = "") }
        }
    }

    /**
     * Shows or hides the dialog warning the user about unsaved changes.
     */
    fun showUnsavedChangesDialog(show: Boolean) {
        val currentState = _uiState.value
        if (currentState is ChartSelectionUiState.Success) {
            _uiState.update { currentState.copy(isUnsavedChangesDialogVisible = show) }
        }
    }

    /**
     * Checks if at least one chart is currently selected.
     * Used to enable or disable the publishing buttons.
     */
    fun hasSelections(): Boolean {
        val currentState = _uiState.value
        return if (currentState is ChartSelectionUiState.Success) {
            currentState.charts.any { it.isSelected }
        } else false
    }
}
