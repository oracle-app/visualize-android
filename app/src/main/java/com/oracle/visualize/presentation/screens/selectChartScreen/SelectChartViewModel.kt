package com.oracle.visualize.presentation.screens.selectChartScreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.oracle.visualize.data.datasources.dtos.ChartResponseDTO
import com.oracle.visualize.domain.usecases.visualization.PublishVisualizationsInBulkUseCase

@HiltViewModel
class SelectChartViewModel @Inject constructor(
    private val repository: AnalyzeRepository,
    private val authRepository: com.oracle.visualize.domain.repositories.AuthRepository,
    private val publishVisualizationsInBulkUseCase: PublishVisualizationsInBulkUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    val taskId: String = savedStateHandle.toRoute<NavRoutes.ChartSelection>().taskId
    private val _uiState = MutableStateFlow<ChartSelectionUiState>(ChartSelectionUiState.Loading)
    val uiState: StateFlow<ChartSelectionUiState> = _uiState.asStateFlow()

    init {
        fetchOverview()
    }

    fun fetchOverview(){
        viewModelScope.launch {
            _uiState.value = ChartSelectionUiState.Loading

            val chartSelections = mutableListOf<ChartSelection>()
            var lastErrorMessageId: Int? = null

            for (chartIndex in 0..4) {
                when (val result = repository.previewedResults(taskId, chartIndex, true)) {
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
                        lastErrorMessageId = when (result.error) {
                            is AppError.NetworkError -> R.string.error_network
                            is AppError.ParsingError -> R.string.error_parsing
                            else -> R.string.error_unknown_retry
                        }
                    }
                }
            }

            if (chartSelections.isEmpty()) {
                _uiState.value = ChartSelectionUiState.Error(lastErrorMessageId ?: R.string.error_viz_not_found)
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

    fun postSelectedChartsToPersonalFeed(onSuccess: () -> Unit, onError: (Int) -> Unit) {
        val state = _uiState.value
        if (state !is ChartSelectionUiState.Success) {
            onError(R.string.error_unknown_retry)
            return
        }
        val selectedCharts = state.charts.filter { it.isSelected }
        if (selectedCharts.isEmpty()) {
            onError(R.string.error_unknown_retry)
            return
        }

        viewModelScope.launch {
            _uiState.value = ChartSelectionUiState.Loading

            val authorId = authRepository.getCurrentUserID() ?: ""

            val firstPageDeferreds = selectedCharts.map { selection ->
                async {
                    repository.getPagedResultsDto(taskId, selection.chartIndex, 1) to selection.chartIndex
                }
            }
            val firstPageResults = firstPageDeferreds.awaitAll()

            val chartPageJobs = mutableListOf<Deferred<Pair<Int, AppResult<ChartResponseDTO>>>>()
            val firstPagesMap = mutableMapOf<Int, ChartResponseDTO>()

            for ((res, chartIndex) in firstPageResults) {
                when (res) {
                    is AppResult.Success -> {
                        val firstPageDto = res.data
                        firstPagesMap[chartIndex] = firstPageDto

                        val totalPages = firstPageDto.totalPages
                        for (page in 2..totalPages) {
                            chartPageJobs.add(
                                async {
                                    chartIndex to repository.getPagedResultsDto(taskId, chartIndex, page)
                                }
                            )
                        }
                    }
                    is AppResult.Error -> {
                        _uiState.value = state
                        val errorId = when(res.error) {
                            is AppError.NetworkError -> R.string.error_network
                            else -> R.string.error_unknown_retry
                        }
                        onError(errorId)
                        return@launch
                    }
                }
            }

            val additionalPagesResults = chartPageJobs.awaitAll()

            val pagesByChart = mutableMapOf<Int, MutableList<ChartResponseDTO>>()
            firstPagesMap.forEach { (chartIndex, firstPageDto) ->
                pagesByChart.getOrPut(chartIndex) { mutableListOf() }.add(firstPageDto)
            }

            for ((chartIndex, res) in additionalPagesResults) {
                when (res) {
                    is AppResult.Success -> {
                        pagesByChart.getOrPut(chartIndex) { mutableListOf() }.add(res.data)
                    }
                    is AppResult.Error -> {
                        _uiState.value = state
                        val errorId = when(res.error) {
                            is AppError.NetworkError -> R.string.error_network
                            else -> R.string.error_unknown_retry
                        }
                        onError(errorId)
                        return@launch
                    }
                }
            }

            val visualizationsToPublish = try {
                withContext(Dispatchers.Default) {
                    selectedCharts.map { selection ->
                        val pagesList = pagesByChart[selection.chartIndex] ?: throw Exception("Missing pages")

                        pagesList.sortBy { it.page }

                        val firstPageDto = pagesList.first()
                        val mergedData = com.oracle.visualize.data.mapper.ChartMapper.mergePagedData(firstPageDto.chartType, pagesList)

                        val previewDto = firstPageDto.copy(chartName = selection.customTitle, preview = true)
                        val combinedDto = firstPageDto.copy(
                            chartName = selection.customTitle,
                            page = 0,
                            preview = false,
                            totalPages = 1,
                            data = mergedData
                        )

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
                    }
                }
            } catch (e: Exception) {
                Log.e("SelectChartViewModel", "Data mapping error", e)
                _uiState.value = state
                onError(R.string.error_parsing)
                return@launch
            }

            when (val publishResult = publishVisualizationsInBulkUseCase(visualizationsToPublish)) {
                is AppResult.Success -> onSuccess()
                is AppResult.Error -> {
                    _uiState.value = state
                    val errorId = when(publishResult.error) {
                        is AppError.NetworkError -> R.string.error_network
                        else -> R.string.error_unknown_retry
                    }
                    onError(errorId)
                }
            }
        }
    }
}

