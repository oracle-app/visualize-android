package com.oracle.visualize.presentation.screens.feedScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.ChartTypes
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var allVisualizations: List<VisualizationCard> = emptyList()

    // TODO: Get from Auth Repository
    private val currentUserID: String = "NQ5fdkRdISA8U7DgcII1"

    init {
        loadData(forceRefresh = false)
    }

    fun loadData(forceRefresh: Boolean = false) {

        if (forceRefresh) {
            allVisualizations = emptyList()
            val current = _uiState.value
            _uiState.value = if (current is FeedUiState.Success) {
                current.copy(isRefreshing = true)
            } else {
                FeedUiState.Loading
            }
        } else {
            _uiState.value = FeedUiState.Loading
        }

        viewModelScope.launch {

            getAllUserVisualizationsUseCase(currentUserID).fold(
                onSuccess = { items ->
                    allVisualizations = items
                    applyLocalFilterAndSearch()
                },
                onFailure = { error ->
                    allVisualizations = emptyList()
                    val uiErrorMessage = when (error) {
                        is AppError.NetworkError -> R.string.error_network
                        is AppError.ParsingError -> R.string.error_parsing
                        is AppError.NotFound     -> R.string.error_viz_not_found
                        else                     -> R.string.error_unknown_retry
                    }
                    Log.e("FeedViewModel", "Error fetching visualizations: ${error.message}", error)
                    _uiState.value = FeedUiState.Error(uiErrorMessage)
                }
            )
        }
    }

    fun onFilterChange(filter: VisualizationFilter) {
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success && currentState.selectedFilter == filter) return

        _uiState.value = when (currentState) {
            is FeedUiState.Success -> currentState.copy(selectedFilter = filter)
            else -> currentState
        }

        if (allVisualizations.isNotEmpty()) {
            applyLocalFilterAndSearch()
        } else {
            loadData()
        }
    }
    fun onSearchTextChange(newText: String) {
        val currentState = _uiState.value
        if(currentState is FeedUiState.Success){
            _uiState.value = currentState.copy(searchText = newText)
        }
        applyLocalFilterAndSearch()
    }

    private fun applyLocalFilterAndSearch() {
        val currentState = _uiState.value
        val filter = if (currentState is FeedUiState.Success)
            currentState.selectedFilter else VisualizationFilter.ALL
        val search = if (currentState is FeedUiState.Success) currentState.searchText else ""


        var filteredItems = when (filter) {
            VisualizationFilter.ALL -> allVisualizations
            VisualizationFilter.PERSONAL -> allVisualizations.filter { it.authorID == currentUserID }
            VisualizationFilter.SHARED -> allVisualizations.filter { it.authorID != currentUserID }
        }

        if (search.isNotBlank()) {
            filteredItems = filteredItems.filter { item ->
                item.title.contains(search, ignoreCase = true)
            }
        }

        _uiState.value = FeedUiState.Success(
            items = filteredItems,
            searchText = search,
            selectedFilter = filter
        )
    }
}
