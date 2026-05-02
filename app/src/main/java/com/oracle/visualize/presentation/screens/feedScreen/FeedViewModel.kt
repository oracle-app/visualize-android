package com.oracle.visualize.presentation.screens.feedScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.ChartTypes
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import com.oracle.visualize.domain.usecases.GetMockChartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Feed screen.
 * Handles fetching visualizations based on filters and search queries.
 *
 * @property getAllUserVisualizationsUseCase Use case to fetch visualizations for a user.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase,
    private val getMockChartUseCase: GetMockChartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUIState())
    val uiState: StateFlow<FeedUIState> = _uiState.asStateFlow()

    private var allItems: List<VisualizationCard> = emptyList()

    init {
        fetchItems(VisualizationFilter.ALL)
    }

    private fun fetchItems(filter: VisualizationFilter) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            /*
            * Get a chart from the mock chart repository.
            *
            * @param chartType: The type of chart, according belongs to the
            * ChartTypes enum.
            *
            * CHART TYPES (Check "domain/models/enums/ChartTypes.kt"):
            * - VERTICAL_BAR (Vertical Bar Chart)
            * - HORIZONTAL_BAR (Vertical Bar Chart)
            * - STACKED_BAR (Stacked Bar Chart)
            * - LINE (Line Chart)
            * - SCATTER (Scatter Chart)
            * - PIE (Pie Chart)
            * - DONUT (Donut Chart)
            *
            * TODO: Get data from the microservice becomes available.
            *
            * */
            val mockChart = getMockChartUseCase(ChartTypes.VERTICAL_BAR).fold(
                onSuccess = { it },
                onFailure = { null }
            )

            // TODO: Get from Auth Repository
            val userID = "oEJtQz0gdbRpTZ8ETPCy"

            getAllUserVisualizationsUseCase(userID, filter).fold(
                onSuccess = { visualizations ->
                    allItems = visualizations.map { item ->
                        item.copy(chart = mockChart)
                    }
                    applySearch()
                },
                onFailure = { error ->
                    allItems = emptyList()

                    val uiErrorMessage = when (error) {
                        is AppError.NetworkError -> "Connection error. Please check your internet."
                        is AppError.ParsingError -> "There was a problem reading some visualizations."
                        is AppError.NotFound -> "No visualizations found."
                        else -> "An unexpected error occurred. Please try again."
                    }

                    Log.e("FeedViewModel", "Error fetching visualizations: ${error.message}", error)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = emptyList(),
                            errorMessage = uiErrorMessage
                        )
                    }
                }
            )
        }
    }

    fun onFilterChange(filter: VisualizationFilter) {
        _uiState.update { it.copy(selectedFilter = filter, searchText = "") }
        fetchItems(filter)
    }

    fun onSearchTextChange(newText: String) {
        _uiState.update { it.copy(searchText = newText) }
        applySearch()
    }

    private fun applySearch() {
        val currentSearch = _uiState.value.searchText
        
        viewModelScope.launch {
            val filteredItems = if (currentSearch.isBlank()) {
                allItems
            } else {
                allItems.filter { item ->
                    item.title.contains(currentSearch, ignoreCase = true)
                }
            }
            _uiState.update { it.copy(items = filteredItems, isLoading = false) }
        }
    }
}