package com.oracle.visualize.presentation.screens.feedScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ** Uncomment the library of the mock chart you want to try out. **

// import com.oracle.visualize.presentation.components.mockHorizontalBarChart
// import com.oracle.visualize.presentation.components.mockVerticalBarChart
// import com.oracle.visualize.presentation.components.mockStackedBarChart
// import com.oracle.visualize.presentation.components.mockLineChart
// import com.oracle.visualize.presentation.components.mockScatterChart
// import com.oracle.visualize.presentation.components.mockPieChart
// import com.oracle.visualize.presentation.components.mockDonutChart
import com.oracle.visualize.presentation.components.mockAreaChart

/**
 * ViewModel for the Feed screen.
 * Handles fetching visualizations based on filters and search queries.
 *
 * @property getAllUserVisualizationsUseCase Use case to fetch visualizations for a user.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase
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

            // TODO: Get from Auth Repository
            val userID = "oEJtQz0gdbRpTZ8ETPCy"

            getAllUserVisualizationsUseCase(userID, filter).fold(
                onSuccess = { visualizations ->
                    allItems = visualizations
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

        /* Mock of a chart model for an item's feed card.
        * You can modify the mock data at RenderChartMock.kt.
        * CAUTION: Only should use one model per item for not
        * to break the component.
        * */
        val itemsWithMockCharts = allItems.map { item ->
            // item.copy(chart = mockVerticalBarChart)
            // item.copy(chart = mockHorizontalBarChart)
            // item.copy(chart = mockStackedBarChart)
            // item.copy(chart = mockPieChart)
            // item.copy(chart = mockDonutChart)
            // item.copy(chart = mockLineChart)
            // item.copy(chart = mockScatterChart)
            item.copy(chart = mockAreaChart)
        }

        val filteredItems = if (currentSearch.isBlank()) {
            itemsWithMockCharts
        } else {
            itemsWithMockCharts.filter { item ->
                item.title.contains(currentSearch, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(items = filteredItems, isLoading = false) }
    }
}