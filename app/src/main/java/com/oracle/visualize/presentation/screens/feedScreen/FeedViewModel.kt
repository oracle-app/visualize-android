package com.oracle.visualize.presentation.screens.feedScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.ChartTypes
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.usecases.DeleteVisualizationForEveryoneUseCase
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import com.oracle.visualize.domain.usecases.GetMockChartUseCase
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
 * Handles fetching visualizations based on filters and search queries,
 * as well as delete/hide operations and dropdown menu state.
 *
 * @property getAllUserVisualizationsUseCase Use case to fetch visualizations for a user.
 * @property getMockChartUseCase Use case to get a mock chart.
 * @property deleteVisualizationForEveryoneUseCase Use case to permanently delete a visualization.
 * @property hideVisualizationForMeUseCase Use case to hide a visualization from the current user's feed.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase,
    private val getMockChartUseCase: GetMockChartUseCase,
    private val deleteVisualizationForEveryoneUseCase: DeleteVisualizationForEveryoneUseCase,
    private val hideVisualizationForMeUseCase: HideVisualizationForMeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUIState())
    val uiState: StateFlow<FeedUIState> = _uiState.asStateFlow()

    private var allVisualizations: List<VisualizationCard> = emptyList()

    // TODO: Get from Auth Repository
    val currentUserID: String = "e9Nk8XrxHJAtwN3Hf2FL"

    init {
        loadData(forceRefresh = false)
    }

    fun loadData(forceRefresh: Boolean = false) {
        if (forceRefresh) {
            allVisualizations = emptyList()
        }

        if (allVisualizations.isEmpty()) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        }

        viewModelScope.launch {
            val mockChart = getMockChartUseCase(chartType = ChartTypes.STACKED_BAR).fold(
                onSuccess = { it },
                onFailure = { null }
            )
            getAllUserVisualizationsUseCase(currentUserID).fold(
                onSuccess = { items ->
                    allVisualizations = items.map { i -> i.copy(chart = mockChart) }
                    applyLocalFilterAndSearch()
                },
                onFailure = { error ->
                    allVisualizations = emptyList()
                    val uiErrorMessage = when (error) {
                        is AppError.NetworkError -> "Connection error. Please check your internet."
                        is AppError.ParsingError -> "There was a problem reading some visualizations."
                        is AppError.NotFound -> "No visualizations found."
                        else -> "An unexpected error occurred. Please try again."
                    }
                    Log.e("FeedViewModel", "Error fetching visualizations: ${error.message}", error)
                    _uiState.update {
                        it.copy(isLoading = false, items = emptyList(), errorMessage = uiErrorMessage)
                    }
                }
            )
        }
    }

    fun onFilterChange(filter: VisualizationFilter) {
        if (_uiState.value.selectedFilter == filter) return
        _uiState.update { it.copy(selectedFilter = filter) }
        if (allVisualizations.isNotEmpty()) applyLocalFilterAndSearch() else loadData()
    }

    fun onSearchTextChange(newText: String) {
        _uiState.update { it.copy(searchText = newText) }
        applyLocalFilterAndSearch()
    }

    // ─── Dropdown menu ────────────────────────────────────────────────────────

    fun onOpenMenu(visualizationId: String) {
        _uiState.update { it.copy(openMenuForId = visualizationId) }
    }

    fun onDismissMenu() {
        _uiState.update { it.copy(openMenuForId = null) }
    }

    fun onShareSelected() {
        _uiState.update { it.copy(openMenuForId = null) }
    }

    // ─── Delete / hide actions ────────────────────────────────────────────────

    /** Called when the user taps "Delete for everyone" in the dropdown (owner only). */
    fun onRequestDeleteForEveryone(visualizationId: String) {
        _uiState.update {
            it.copy(
                openMenuForId = null,
                deleteDialogState = DeleteDialogState.ShowDeleteForEveryone(visualizationId)
            )
        }
    }

    /** Called when the user taps "Delete for me" in the dropdown (non-owner). */
    fun onRequestDeleteForMe(visualizationId: String) {
        _uiState.update {
            it.copy(
                openMenuForId = null,
                deleteDialogState = DeleteDialogState.ShowDeleteForMe(visualizationId)
            )
        }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(deleteDialogState = DeleteDialogState.Hidden) }
    }

    fun onConfirmDeleteForEveryone(visualizationId: String) {
        _uiState.update { it.copy(deleteDialogState = DeleteDialogState.Hidden) }
        viewModelScope.launch {
            deleteVisualizationForEveryoneUseCase(visualizationId).fold(
                onSuccess = { loadData(forceRefresh = true) },
                onFailure = { error ->
                    Log.e("FeedViewModel", "Failed to delete visualization: ${error.message}", error)
                }
            )
        }
    }

    fun onConfirmDeleteForMe(visualizationId: String) {
        _uiState.update { it.copy(deleteDialogState = DeleteDialogState.Hidden) }
        viewModelScope.launch {
            hideVisualizationForMeUseCase(currentUserID, visualizationId).fold(
                onSuccess = { loadData(forceRefresh = true) },
                onFailure = { error ->
                    Log.e("FeedViewModel", "Failed to hide visualization: ${error.message}", error)
                }
            )
        }
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private fun applyLocalFilterAndSearch() {
        val filter = _uiState.value.selectedFilter
        val search = _uiState.value.searchText

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

        _uiState.update {
            it.copy(items = filteredItems, isLoading = false, errorMessage = null)
        }
    }
}
