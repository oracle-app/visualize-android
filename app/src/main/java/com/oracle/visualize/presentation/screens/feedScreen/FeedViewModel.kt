package com.oracle.visualize.presentation.screens.feedScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.FeedItem
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.ObserveUserFeedUseCase
import com.oracle.visualize.domain.usecases.ParseSingleChartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val observeUserFeedUseCase: ObserveUserFeedUseCase,
    private val authRepository: AuthRepository,
    private val parseSingleChartUseCase: ParseSingleChartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var allFeedItems: List<FeedItem> = emptyList()
    private var currentUserID: String = ""
    private var feedJob: Job? = null

    init {
        try {
            currentUserID = authRepository.getCurrentUserID()
            loadData(forceRefresh = false)
        } catch (e: Exception) {
            _uiState.value = FeedUiState.Error(R.string.error_unknown_retry)
        }
    }

    fun toggleSearch() {
        _uiState.update { currentState ->
            if (currentState is FeedUiState.Success) {
                currentState.copy(isSearching = !currentState.isSearching)
            } else currentState
        }
    }

    fun loadChartForCard(card: VisualizationCard) {
        viewModelScope.launch {
            val chart = parseSingleChartUseCase(card)
            allFeedItems = allFeedItems.map { item ->
                if (item.card.id == card.id) {
                    item.copy(chart = chart, isChartLoading = false)
                } else {
                    item
                }
            }
            applyLocalFilterAndSearch()
        }
    }

    fun loadData(forceRefresh: Boolean = false) {
        val current = _uiState.value
        _uiState.value = if (forceRefresh && current is FeedUiState.Success) {
            current.copy(isRefreshing = true)
        } else if (!forceRefresh) {
            FeedUiState.Loading
        } else {
            current
        }

        feedJob?.cancel()
        feedJob = viewModelScope.launch {
            observeUserFeedUseCase(currentUserID, forceRefresh).collect { result ->
                result.fold(
                    onSuccess = { items ->
                        allFeedItems = items
                        applyLocalFilterAndSearch()
                    },
                    onFailure = { error ->
                        allFeedItems = emptyList()
                        val uiErrorMessage = when (error) {
                            is AppError.NetworkError -> R.string.error_network
                            is AppError.ParsingError -> R.string.error_parsing
                            is AppError.NotFound     -> R.string.error_viz_not_found
                            else                     -> R.string.error_unknown_retry
                        }
                        _uiState.value = FeedUiState.Error(uiErrorMessage)
                    }
                )
            }
        }
    }

    fun onFilterChange(filter: VisualizationFilter) {
        _uiState.update { state ->
            if (state is FeedUiState.Success && state.selectedFilter != filter) {
                applyFiltersToState(state.copy(selectedFilter = filter))
            } else state
        }
    }

    fun onSearchTextChange(newText: String) {
        _uiState.update { state ->
            if (state is FeedUiState.Success) {
                applyFiltersToState(state.copy(searchText = newText))
            } else state
        }
    }

    private fun applyLocalFilterAndSearch() {
        _uiState.update { state ->
            val successState = state as? FeedUiState.Success ?: FeedUiState.Success(
                items = allFeedItems,
                currentUserID = currentUserID
            )
            applyFiltersToState(successState)
        }
    }

    private fun applyFiltersToState(currentState: FeedUiState.Success): FeedUiState.Success {
        var filteredItems = when (currentState.selectedFilter) {
            VisualizationFilter.ALL -> allFeedItems
            VisualizationFilter.PERSONAL -> allFeedItems.filter { it.card.authorID == currentUserID }
            VisualizationFilter.SHARED -> allFeedItems.filter { it.card.authorID != currentUserID }
        }

        if (currentState.searchText.isNotBlank()) {
            filteredItems = filteredItems.filter { item ->
                item.card.title.contains(currentState.searchText, ignoreCase = true)
            }
        }
        return currentState.copy(items = filteredItems)
    }
}
