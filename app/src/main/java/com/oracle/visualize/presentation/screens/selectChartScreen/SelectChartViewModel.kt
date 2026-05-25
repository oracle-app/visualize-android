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
import com.oracle.visualize.data.datasources.dtos.ChartResponseDTO

/**
 * ViewModel for the Select Chart screen.
 * Manages the selection of visualizations from a list.
 */
@HiltViewModel
class SelectChartViewModel @Inject constructor(
    private val repository: AnalyzeRepository,
    private val authRepository: com.oracle.visualize.domain.repositories.AuthRepository,
    private val publishVisualizationsInBulkUseCase: com.oracle.visualize.domain.usecases.PublishVisualizationsInBulkUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    val taskId: String = savedStateHandle.toRoute<NavRoutes.ChartSelection>().taskId
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
                                    chartIndex = chartIndex,
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
    fun postSelectedChartsToPersonalFeed(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        if (state !is ChartSelectionUiState.Success) {
            onError("Invalid state")
            return
        }
        val selectedCharts = state.charts.filter { it.isSelected }
        if (selectedCharts.isEmpty()) {
            onError("No charts selected")
            return
        }
        viewModelScope.launch {
            _uiState.value = ChartSelectionUiState.Loading
            try {
                val authorId = authRepository.getCurrentUserID()
                val visualizationsToPublish = mutableListOf<com.oracle.visualize.domain.models.Visualization>()
                for (selection in selectedCharts) {
                    val pagesList = mutableListOf<ChartResponseDTO>()

                    // 1. Fetch Page 1 DTO
                    val firstPageRes = repository.getPagedResultsDto(taskId, selection.chartIndex, 1)
                    if (firstPageRes !is AppResult.Success) {
                        throw Exception((firstPageRes as? AppResult.Error)?.error?.message ?: "Failed to fetch page 1")
                    }

                    val firstPageDto = firstPageRes.data
                    pagesList.add(firstPageDto)

                    // 2. Fetch pages 2 to totalPages
                    val totalPages = firstPageDto.totalPages
                    for (page in 2..totalPages) {
                        val pageRes = repository.getPagedResultsDto(taskId, selection.chartIndex, page)
                        if (pageRes is AppResult.Success) {
                            pagesList.add(pageRes.data)
                        } else {
                            throw Exception((pageRes as? AppResult.Error)?.error?.message ?: "Failed to fetch page $page")
                        }
                    }
                    // 3. Merge paged data
                    val mergedData = com.oracle.visualize.data.mapper.ChartMapper.mergePagedData(firstPageDto.chartType, pagesList)
                    // 4. Construct preview and full JSON objects using custom title
                    val previewDto = firstPageDto.copy(chartName = selection.customTitle)
                    val combinedDto = firstPageDto.copy(
                        chartName = selection.customTitle,
                        page = 0,
                        preview = false,
                        totalPages = 1,
                        data = mergedData
                    )
                    visualizationsToPublish.add(
                        com.oracle.visualize.domain.models.Visualization(
                            id = "",
                            authorID = authorId,
                            title = selection.customTitle,
                            configJSON = com.google.gson.Gson().toJson(combinedDto),
                            previewJSON = com.google.gson.Gson().toJson(previewDto),
                            sharedWithUsers = emptyList(),
                            sharedWithTeams = emptyList(),
                            createdAt = java.util.Date()
                        )
                    )
                }
                publishVisualizationsInBulkUseCase(visualizationsToPublish).fold(
                    onSuccess = { onSuccess() },
                    onFailure = { throw it }
                )
            } catch (e: Exception) {
                Log.e("SelectChartViewModel", "Failed to publish visualizations", e)
                _uiState.value = state
                onError(e.message ?: "An unexpected error occurred while posting.")
            }
        }
    }
}
