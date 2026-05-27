package com.oracle.visualize.presentation.screens.feedScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.data.datasources.local.FeedCacheManager
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.FeedItem
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.DeleteVisualizationForEveryoneUseCase
import com.oracle.visualize.domain.usecases.HideVisualizationForMeUseCase
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
    private val deleteVisualizationForEveryoneUseCase: DeleteVisualizationForEveryoneUseCase,
    private val hideVisualizationForMeUseCase: HideVisualizationForMeUseCase,
    private val authRepository: AuthRepository,
    private val parseSingleChartUseCase: ParseSingleChartUseCase,
    private val feedCacheManager: FeedCacheManager
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

    // ─── Data loading ──────────────────────────────────────────────────────────

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
                        allFeedItems = items.distinctBy { it.card.id }
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

    fun refreshIfCacheInvalidated() {
        if (feedCacheManager.cachedFeed == null) {
            loadData(forceRefresh = true)
        }
    }

    fun loadChartForCard(card: VisualizationCard) {
        viewModelScope.launch {
            val chart = parseSingleChartUseCase(card)
            allFeedItems = allFeedItems.map { item ->
                if (item.card.id == card.id) item.copy(chart = chart, isChartLoading = false)
                else item
            }
            applyLocalFilterAndSearch()
        }
    }

    // ─── Search & filter ───────────────────────────────────────────────────────

    fun toggleSearch() {
        _uiState.update { state ->
            if (state is FeedUiState.Success) state.copy(isSearching = !state.isSearching)
            else state
        }
    }

    fun onFilterChange(filter: VisualizationFilter) {
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success && currentState.selectedFilter == filter) return
        _uiState.value = when (currentState) {
            is FeedUiState.Success -> currentState.copy(selectedFilter = filter)
            else -> currentState
        }
        if (allFeedItems.isNotEmpty()) applyLocalFilterAndSearch() else loadData()
    }

    fun onSearchTextChange(newText: String) {
        val currentState = _uiState.value
        if (currentState is FeedUiState.Success) {
            _uiState.value = currentState.copy(searchText = newText)
        }
        applyLocalFilterAndSearch()
    }

    // ─── Card menu ─────────────────────────────────────────────────────────────

    fun onMenuOpen(visualizationId: String) = updateSuccess {
        it.copy(menuOpenForId = visualizationId)
    }

    fun onMenuDismiss() = updateSuccess {
        it.copy(menuOpenForId = null)
    }

    fun onRequestShare(visualizationId: String) = updateSuccess {
        it.copy(menuOpenForId = null, pendingShareId = visualizationId)
    }

    fun onShareNavigated() = updateSuccess {
        it.copy(pendingShareId = null)
    }

    fun onRequestDeleteForEveryone(visualizationId: String) = updateSuccess {
        it.copy(menuOpenForId = null, deleteDialogForId = visualizationId)
    }

    fun onRequestHideForMe(visualizationId: String) = updateSuccess {
        it.copy(menuOpenForId = null, hideDialogForId = visualizationId)
    }

    fun onDismissDialog() = updateSuccess {
        it.copy(deleteDialogForId = null, hideDialogForId = null)
    }

    fun onConfirmDeleteForEveryone(visualizationId: String) {
        updateSuccess { it.copy(deleteDialogForId = null) }
        viewModelScope.launch {
            deleteVisualizationForEveryoneUseCase(visualizationId).fold(
                onSuccess = {
                    feedCacheManager.clearCache()
                    allFeedItems = allFeedItems.filter { it.card.id != visualizationId }
                    applyLocalFilterAndSearch()
                },
                onFailure = { _uiState.value = FeedUiState.Error(R.string.error_unknown_retry) }
            )
        }
    }

    fun onConfirmHideForMe(visualizationId: String) {
        updateSuccess { it.copy(hideDialogForId = null) }
        viewModelScope.launch {
            hideVisualizationForMeUseCase(currentUserID, visualizationId).fold(
                onSuccess = {
                    feedCacheManager.clearCache()
                    allFeedItems = allFeedItems.filter { it.card.id != visualizationId }
                    applyLocalFilterAndSearch()
                },
                onFailure = { _uiState.value = FeedUiState.Error(R.string.error_unknown_retry) }
            )
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private fun applyLocalFilterAndSearch() {
        val currentState = _uiState.value
        val filter      = if (currentState is FeedUiState.Success) currentState.selectedFilter else VisualizationFilter.ALL
        val search      = if (currentState is FeedUiState.Success) currentState.searchText else ""
        val isSearching = if (currentState is FeedUiState.Success) currentState.isSearching else false

        var filtered = when (filter) {
            VisualizationFilter.ALL      -> allFeedItems
            VisualizationFilter.PERSONAL -> allFeedItems.filter { it.card.authorID == currentUserID }
            VisualizationFilter.SHARED   -> allFeedItems.filter { it.card.authorID != currentUserID }
        }

        if (search.isNotBlank()) {
            filtered = filtered.filter { it.card.title.contains(search, ignoreCase = true) }
        }

        val isDeletableMap = filtered.associate { it.card.id to (it.card.authorID == currentUserID) }

        _uiState.update { state ->
            if (state is FeedUiState.Success) {
                state.copy(
                    items          = filtered,
                    searchText     = search,
                    selectedFilter = filter,
                    isRefreshing   = false,
                    isSearching    = isSearching,
                    isDeletableMap = isDeletableMap
                )
            } else {
                FeedUiState.Success(
                    items          = filtered,
                    searchText     = search,
                    selectedFilter = filter,
                    isRefreshing   = false,
                    isSearching    = isSearching,
                    isDeletableMap = isDeletableMap
                )
            }
        }
    }

    private fun updateSuccess(block: (FeedUiState.Success) -> FeedUiState.Success) {
        val current = _uiState.value as? FeedUiState.Success ?: return
        _uiState.value = block(current)
    }
}
