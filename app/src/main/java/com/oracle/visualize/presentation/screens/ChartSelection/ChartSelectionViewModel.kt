package com.oracle.visualize.presentation.screens.ChartSelection

import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.ChartSelectionUiState
import com.oracle.visualize.domain.models.Visualization
import com.oracle.visualize.domain.models.VisualizationSelection
import com.oracle.visualize.domain.models.VisualizationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.UUID

class ChartSelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChartSelectionUiState())
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    init {
        loadMockCharts()
    }

    private fun loadMockCharts() {
        // Simulating loading data from a repository or use case
        // Mock data for development reference
        val mockCharts = listOf(
            Visualization(
                id = UUID.randomUUID().toString(),
                ownerId = "user1",
                title = "Commerce Activity: Units Sold vs Total Transactions",
                type = VisualizationType.COMBINED,
                configJson = emptyMap(),
                sharedWith = emptyList(),
                sharedWithGroup = emptyList(),
                commentCount = 0,
                hasNewActivity = false,
                createdAt = Date()
            ),
            Visualization(
                id = UUID.randomUUID().toString(),
                ownerId = "user1",
                title = "Units Sold vs Total Transactions",
                type = VisualizationType.BAR,
                configJson = emptyMap(),
                sharedWith = emptyList(),
                sharedWithGroup = emptyList(),
                commentCount = 0,
                hasNewActivity = false,
                createdAt = Date()
            ),
             Visualization(
                id = UUID.randomUUID().toString(),
                ownerId = "user1",
                title = "Commercial Performance Overview: Comparison Between Units Sold and Total Transaction Volume",
                type = VisualizationType.COMBINED,
                configJson = emptyMap(),
                sharedWith = emptyList(),
                sharedWithGroup = emptyList(),
                commentCount = 0,
                hasNewActivity = false,
                createdAt = Date()
            )
        ).map { VisualizationSelection(it) }

        _uiState.update { it.copy(charts = mockCharts) }
    }

    fun toggleSelection(chartId: String) {
        _uiState.update { state ->
            state.copy(
                charts = state.charts.map { 
                    if (it.visualization.id == chartId) it.copy(isSelected = !it.isSelected) 
                    else it 
                }
            )
        }
    }

    fun updateChartTitle(chartId: String, newTitle: String) {
        _uiState.update { state ->
            state.copy(
                charts = state.charts.map { 
                    if (it.visualization.id == chartId) {
                        it.copy(visualization = it.visualization.copy(title = newTitle))
                    } else it 
                }
            )
        }
    }

    fun showUnsavedChangesDialog(show: Boolean) {
        _uiState.update { it.copy(isUnsavedChangesDialogVisible = show) }
    }

    fun hasSelections(): Boolean {
        return _uiState.value.charts.any { it.isSelected }
    }
}
