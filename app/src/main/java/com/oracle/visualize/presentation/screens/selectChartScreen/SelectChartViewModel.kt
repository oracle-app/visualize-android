package com.oracle.visualize.presentation.screens.selectChartScreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.AnalyzeRepository
import com.oracle.visualize.presentation.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Select Chart screen.
 * Manages the selection of visualizations from a list.
 */
@HiltViewModel
class SelectChartViewModel @Inject constructor(
    private val repository: AnalyzeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    private val taskId: String = savedStateHandle.toRoute<NavRoutes.ChartSelection>().taskId
    private val _uiState = MutableStateFlow<ChartSelectionUiState>(ChartSelectionUiState.Loading)
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    init {
        fetchOverview()
    }
    private fun fetchOverview(){
        viewModelScope.launch {
            _uiState.value = ChartSelectionUiState.Loading
            
            // We'll collect charts that successfully load
            val chartSelections = mutableListOf<ChartSelection>()
            var lastErrorMessage: String? = null

            // Fetch the 5 specific charts (indices 0 to 4) using the previewedResults endpoint
            for (chartIndex in 0..4) {
                val result = repository.previewedResults(taskId, chartIndex, true)
                when (result) {
                    is AppResult.Success -> {
                        result.data?.let { chart ->
                            chartSelections.add(
                                ChartSelection(
                                    chart = chart,
                                    customTitle = chart.chartTitle,
                                    isSelected = true,
                                )
                            )
                        }
                    }
                    is AppResult.Error -> {
                        Log.e("SelectChartViewModel", "Error fetching chart $chartIndex: ${result.error.message}")
                        lastErrorMessage = result.error.message
                    }
                }
            }

            if (chartSelections.isEmpty()) {
                _uiState.value = ChartSelectionUiState.Error(lastErrorMessage ?: "No charts could be generated for this dataset.")
            } else {
                _uiState.value = ChartSelectionUiState.Success(charts = chartSelections)
            }
        }
    }
    fun toggleSelection(chartId: String) {
        _uiState.update { currentState ->
            if (currentState is ChartSelectionUiState.Success) {
                currentState.copy(
                    charts = currentState.charts.map { 
                        if (it.id == chartId) it.copy(isSelected = !it.isSelected) 
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
                        if (it.id == chartId) {
                            it.copy(customTitle = newTitle)
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
