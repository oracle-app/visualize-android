package com.oracle.visualize.presentation.screens.feedScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
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
 * Handles fetching visualizations, filter/search, the per-card options menu,
 * and delete (for everyone / for me) confirmation dialogs.
 *
 * @property getAllUserVisualizationsUseCase Fetches all visualizations for the current user.
 * @property deleteVisualizationForEveryoneUseCase Permanently deletes a visualization.
 * @property hideVisualizationForMeUseCase Hides a visualization from the current user's feed.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase,
    private val deleteVisualizationForEveryoneUseCase: DeleteVisualizationForEveryoneUseCase,
    private val hideVisualizationForMeUseCase: HideVisualizationForMeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUIState())
    val uiState: StateFlow<FeedUIState> = _uiState.asStateFlow()

    private var allVisualizations: List<VisualizationCard> = emptyList()

    // TODO: Replace with Auth repository
    val currentUserID: String = "e9Nk8XrxHJAtwN3Hf2FL"

    init {
        loadData(forceRefresh = false)
    }

    // ─── Data loading ─────────────────────────────────────────────────────────

    fun loadData(forceRefresh: Boolean = false) {
        if (forceRefresh) allVisualizations = emptyList()

        if (allVisualizations.isEmpty()) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
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
                        is AppError.NetworkError -> "Connection error. Please check your internet."
                        is AppError.ParsingError -> "There was a problem reading some visualizations."
                        is AppError.NotFound     -> "No visualizations found."
                        else                     -> "An unexpected error occurred. Please try again."
                    }
                    _uiState.update {
                        it.copy(isLoading = false, items = emptyList(), errorMessage = uiErrorMessage)
                    }
                }
            )
        }
    }

    // ─── Filter / search ──────────────────────────────────────────────────────

    fun onFilterChange(filter: VisualizationFilter) {
        if (_uiState.value.selectedFilter == filter) return
        _uiState.update { it.copy(selectedFilter = filter) }
        if (allVisualizations.isNotEmpty()) applyLocalFilterAndSearch() else loadData()
    }

    fun onSearchTextChange(newText: String) {
        _uiState.update { it.copy(searchText = newText) }
        applyLocalFilterAndSearch()
    }

    private fun applyLocalFilterAndSearch() {
        val filter = _uiState.value.selectedFilter
        val search = _uiState.value.searchText

        var filtered = when (filter) {
            VisualizationFilter.ALL      -> allVisualizations
            VisualizationFilter.PERSONAL -> allVisualizations.filter { it.authorID == currentUserID }
            VisualizationFilter.SHARED   -> allVisualizations.filter { it.authorID != currentUserID }
        }

        if (search.isNotBlank()) {
            filtered = filtered.filter { it.title.contains(search, ignoreCase = true) }
        }

        _uiState.update { it.copy(items = filtered, isLoading = false, errorMessage = null) }
    }

    // ─── Options menu ─────────────────────────────────────────────────────────

    fun onOpenMenu(visualizationId: String) {
        _uiState.update { it.copy(openMenuForId = visualizationId) }
    }

    fun onDismissMenu() {
        _uiState.update { it.copy(openMenuForId = null) }
    }

    // ─── Delete dialogs ───────────────────────────────────────────────────────

    fun onRequestDeleteForMe(visualizationId: String) {
        _uiState.update {
            it.copy(
                openMenuForId     = null,
                deleteDialogState = DeleteDialogState.ShowDeleteForMe(visualizationId)
            )
        }
    }

    fun onRequestDeleteForEveryone(visualizationId: String) {
        _uiState.update {
            it.copy(
                openMenuForId     = null,
                deleteDialogState = DeleteDialogState.ShowDeleteForEveryone(visualizationId)
            )
        }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(deleteDialogState = DeleteDialogState.Hidden) }
    }

    fun onConfirmDeleteForMe(visualizationId: String) {
        _uiState.update { it.copy(deleteDialogState = DeleteDialogState.Hidden) }
        viewModelScope.launch {
            hideVisualizationForMeUseCase(currentUserID, visualizationId).fold(
                onSuccess = {
                    allVisualizations = allVisualizations.filter { it.id != visualizationId }
                    applyLocalFilterAndSearch()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = e.message ?: "Failed to hide visualization") }
                }
            )
        }
    }

    fun onConfirmDeleteForEveryone(visualizationId: String) {
        _uiState.update { it.copy(deleteDialogState = DeleteDialogState.Hidden) }
        viewModelScope.launch {
            deleteVisualizationForEveryoneUseCase(visualizationId).fold(
                onSuccess = {
                    allVisualizations = allVisualizations.filter { it.id != visualizationId }
                    applyLocalFilterAndSearch()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = e.message ?: "Failed to delete visualization") }
                }
            )
        }
    }

    // ─── Share ────────────────────────────────────────────────────────────────

    fun onShareSelected() {
        _uiState.update { it.copy(openMenuForId = null) }
    }
}
