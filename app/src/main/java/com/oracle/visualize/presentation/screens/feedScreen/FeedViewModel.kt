package com.oracle.visualize.presentation.screens.feedScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.DeleteVisualizationForEveryoneUseCase
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import com.oracle.visualize.domain.usecases.HideVisualizationForMeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Feed screen.
 * Handles fetching visualizations, filtering, searching,
 * and the delete/hide/share actions triggered from the card menu.
 *
 * @property getAllUserVisualizationsUseCase Fetches all visualizations for the current user.
 * @property deleteVisualizationForEveryoneUseCase Permanently deletes a visualization (owner only).
 * @property hideVisualizationForMeUseCase Hides a visualization from the current user's feed.
 * @property authRepository Provides the current user ID.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase,
    private val deleteVisualizationForEveryoneUseCase: DeleteVisualizationForEveryoneUseCase,
    private val hideVisualizationForMeUseCase: HideVisualizationForMeUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var allVisualizations: List<VisualizationCard> = emptyList()
    private var currentUserID: String = ""

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
                    allVisualizations = items.distinctBy { it.id }
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

                    _uiState.value = FeedUiState.Error(uiErrorMessage)
                }
            )
        }
    }

    // ─── Search & filter ───────────────────────────────────────────────────────

    fun toggleSearch() {
        _uiState.update { state ->
            if (state is FeedUiState.Success) {
                state.copy(isSearching = !state.isSearching)
            } else {
                state
            }
        }
    }

    fun onFilterChange(filter: VisualizationFilter) {
        val currentState = _uiState.value

        if (
            currentState is FeedUiState.Success &&
            currentState.selectedFilter == filter
        ) return

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

        if (currentState is FeedUiState.Success) {
            _uiState.value = currentState.copy(searchText = newText)
        }

        applyLocalFilterAndSearch()
    }

    // ─── Card menu ─────────────────────────────────────────────────────────────

    /** Opens the three-dot dropdown for the given card. */
    fun onMenuOpen(visualizationId: String) = updateSuccess {
        it.copy(menuOpenForId = visualizationId)
    }

    /** Closes the dropdown without any action. */
    fun onMenuDismiss() = updateSuccess {
        it.copy(menuOpenForId = null)
    }

    /** Sets pendingShareId so the View can navigate safely on next frame. */
    fun onRequestShare(visualizationId: String) = updateSuccess {
        it.copy(
            menuOpenForId = null,
            pendingShareId = visualizationId
        )
    }

    /** Clears pendingShareId after navigation has been handled. */
    fun onShareNavigated() = updateSuccess {
        it.copy(pendingShareId = null)
    }

    /** Shows the "Delete for everyone" confirmation dialog. */
    fun onRequestDeleteForEveryone(visualizationId: String) = updateSuccess {
        it.copy(
            menuOpenForId = null,
            deleteDialogForId = visualizationId
        )
    }

    /** Shows the "Hide for me" confirmation dialog. */
    fun onRequestHideForMe(visualizationId: String) = updateSuccess {
        it.copy(
            menuOpenForId = null,
            hideDialogForId = visualizationId
        )
    }

    /** Dismisses any open confirmation dialog. */
    fun onDismissDialog() = updateSuccess {
        it.copy(
            deleteDialogForId = null,
            hideDialogForId = null
        )
    }

    /** Permanently deletes the visualization for every user. */
    fun onConfirmDeleteForEveryone(visualizationId: String) {
        updateSuccess {
            it.copy(deleteDialogForId = null)
        }

        viewModelScope.launch {
            deleteVisualizationForEveryoneUseCase(visualizationId).fold(
                onSuccess = {
                    allVisualizations =
                        allVisualizations.filter { it.id != visualizationId }

                    applyLocalFilterAndSearch()
                },
                onFailure = {
                    _uiState.value =
                        FeedUiState.Error(R.string.error_unknown_retry)
                }
            )
        }
    }

    /** Hides the visualization from the current user's feed only. */
    fun onConfirmHideForMe(visualizationId: String) {
        updateSuccess {
            it.copy(hideDialogForId = null)
        }

        viewModelScope.launch {
            hideVisualizationForMeUseCase(
                currentUserID,
                visualizationId
            ).fold(
                onSuccess = {
                    allVisualizations =
                        allVisualizations.filter { it.id != visualizationId }

                    applyLocalFilterAndSearch()
                },
                onFailure = {
                    _uiState.value =
                        FeedUiState.Error(R.string.error_unknown_retry)
                }
            )
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private fun applyLocalFilterAndSearch() {
        val currentState = _uiState.value

        val filter =
            if (currentState is FeedUiState.Success) {
                currentState.selectedFilter
            } else {
                VisualizationFilter.ALL
            }

        val search =
            if (currentState is FeedUiState.Success) {
                currentState.searchText
            } else {
                ""
            }

        var filtered = when (filter) {
            VisualizationFilter.ALL ->
                allVisualizations

            VisualizationFilter.PERSONAL ->
                allVisualizations.filter {
                    it.authorID == currentUserID
                }

            VisualizationFilter.SHARED ->
                allVisualizations.filter {
                    it.authorID != currentUserID
                }
        }

        if (search.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(search, ignoreCase = true)
            }
        }

        _uiState.update { state ->
            if (state is FeedUiState.Success) {
                state.copy(
                    items = filtered,
                    searchText = search,
                    selectedFilter = filter,
                    isRefreshing = false,
                    currentUserID = currentUserID
                )
            } else {
                FeedUiState.Success(
                    items = filtered,
                    searchText = search,
                    selectedFilter = filter,
                    isRefreshing = false,
                    currentUserID = currentUserID
                )
            }
        }
    }

    private fun updateSuccess(
        block: (FeedUiState.Success) -> FeedUiState.Success
    ) {
        val current = _uiState.value as? FeedUiState.Success ?: return
        _uiState.value = block(current)
    }
}
