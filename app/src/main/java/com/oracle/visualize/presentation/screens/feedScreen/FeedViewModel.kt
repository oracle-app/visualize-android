package com.oracle.visualize.presentation.screens.feedScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.data.datasources.local.FeedCacheManager
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.models.FeedItem
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.UserType
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.models.policyObjects.VisualizationPermissions
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.domain.usecases.ObserveUserFeedUseCase
import com.oracle.visualize.domain.usecases.chart.ParseSingleChartUseCase
import com.oracle.visualize.domain.usecases.visualization.DeleteVisualizationForEveryoneUseCase
import com.oracle.visualize.domain.usecases.visualization.HideVisualizationForMeUseCase
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
    private val userRepository: UserRepository,
    private val parseSingleChartUseCase: ParseSingleChartUseCase,
    private val deleteVisualizationForEveryoneUseCase: DeleteVisualizationForEveryoneUseCase,
    private val hideVisualizationForMeUseCase: HideVisualizationForMeUseCase,
    private val feedCacheManager: FeedCacheManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var allFeedItems: List<FeedItem> = emptyList()
    private var currentUserID: String = ""
    private var currentUserType: UserType = UserType.CONSUMER
    private var feedJob: Job? = null

    init {
        currentUserID = authRepository.getCurrentUserID() ?: ""

        if (currentUserID.isBlank()) {
            _uiState.value = FeedUiState.Error(R.string.error_unknown_retry)
        } else {
            viewModelScope.launch {
                try {
                    when (val userResult = userRepository.getUserByUserID(currentUserID)) {
                        is AppResult.Success -> {
                            currentUserType = userResult.data.userType
                            loadData(forceRefresh = false)
                        }
                        is AppResult.Error -> {
                            _uiState.value = FeedUiState.Error(R.string.error_unknown_retry)
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = FeedUiState.Error(R.string.error_unknown_retry)
                }
            }
        }
    }

    // ─── Data loading ──────────────────────────────────────────────────────────

    fun toggleSearch() {
        _uiState.update { currentState ->
            if (currentState is FeedUiState.Success) {
                currentState.copy(isSearching = !currentState.isSearching)
            } else currentState
        }
    }

    fun loadChartForCard(card: VisualizationCard) {
        viewModelScope.launch {
            val chart = when (val result = parseSingleChartUseCase(card)) {
                is AppResult.Success -> result.data
                is AppResult.Error -> null
            }

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
                when (result) {
                    is AppResult.Success -> {
                        allFeedItems = result.data.distinctBy { it.card.id }
                        applyLocalFilterAndSearch()
                    }
                    is AppResult.Error -> {
                        allFeedItems = emptyList()
                        val uiErrorMessage = when (result.error) {
                            is AppError.NetworkError -> R.string.error_network
                            is AppError.ParsingError -> R.string.error_parsing
                            is AppError.NotFound     -> R.string.error_viz_not_found
                            else                     -> R.string.error_unknown_retry
                        }
                        _uiState.value = FeedUiState.Error(uiErrorMessage)
                    }
                }
            }
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

    // ─── Cache ─────────────────────────────────────────────────────────────────

    /**
     * Called when the Feed screen resumes (e.g. after navigating back from ShareWithTeammates).
     * If [FeedCacheManager] was cleared by another screen (like after a successful share),
     * forces a full reload so the feed reflects the latest shared users immediately.
     */
    fun refreshIfCacheInvalidated() {
        if (feedCacheManager.cachedFeed == null) {
            loadData(forceRefresh = true)
        }
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

            when (val result = deleteVisualizationForEveryoneUseCase(visualizationId)) {
                is AppResult.Success -> {
                    feedCacheManager.clearCache()
                    allFeedItems = allFeedItems.filter { it.card.id != visualizationId }
                    applyLocalFilterAndSearch()
                }
                is AppResult.Error -> {
                    _uiState.value = FeedUiState.Error(R.string.error_unknown_retry)
                }
            }
        }
    }

    fun onConfirmHideForMe(visualizationId: String) {
        updateSuccess { it.copy(hideDialogForId = null) }
        viewModelScope.launch {
            when (val result = hideVisualizationForMeUseCase(currentUserID, visualizationId)) {
                is AppResult.Success -> {
                    feedCacheManager.clearCache()
                    allFeedItems = allFeedItems.filter { it.card.id != visualizationId }
                    applyLocalFilterAndSearch()
                }
                is AppResult.Error -> {
                    _uiState.value = FeedUiState.Error(R.string.error_unknown_retry)
                }
            }
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private fun applyLocalFilterAndSearch() {
        val currentState = _uiState.value
        val filter      = if (currentState is FeedUiState.Success) currentState.selectedFilter else VisualizationFilter.ALL
        val search      = if (currentState is FeedUiState.Success) currentState.searchText else ""
        val isSearching = if (currentState is FeedUiState.Success) currentState.isSearching else false

        var filteredItems = when (filter) {
            VisualizationFilter.ALL      -> allFeedItems
            VisualizationFilter.PERSONAL -> allFeedItems.filter { it.card.authorID == currentUserID }
            VisualizationFilter.SHARED   -> allFeedItems.filter { it.card.authorID != currentUserID }
        }

        if (search.isNotBlank()) {
            filteredItems = filteredItems.filter { item ->
                item.card.title.contains(search, ignoreCase = true)
            }
        }

        val permissionsMap = filteredItems.associate { item ->
            item.card.id to VisualizationPermissions(
                userType = currentUserType,
                currentUserID = currentUserID,
                authorID = item.card.authorID
            )
        }

        _uiState.update {
            FeedUiState.Success(
                items          = filteredItems,
                currentUserID  = currentUserID,
                searchText     = search,
                selectedFilter = filter,
                isRefreshing   = false,
                isSearching    = isSearching,
                permissionsMap = permissionsMap
            )
        }
    }

    private fun updateSuccess(block: (FeedUiState.Success) -> FeedUiState.Success) {
        val current = _uiState.value as? FeedUiState.Success ?: return
        _uiState.value = block(current)
    }
}
