package com.oracle.visualize.presentation.screens.feedScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.domain.usecases.DeleteVisualizationForEveryoneUseCase
import com.oracle.visualize.domain.usecases.GetAllUserVisualizationsUseCase
import com.oracle.visualize.domain.usecases.HideVisualizationForMeUseCase
import com.oracle.visualize.presentation.components.FeedCard
import com.oracle.visualize.presentation.components.FeedTopBar
import com.oracle.visualize.presentation.components.SearchSection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Feed screen.
 * Handles fetching visualizations, filter/search,
 * menus and delete dialogs.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getAllUserVisualizationsUseCase: GetAllUserVisualizationsUseCase,
    private val deleteVisualizationForEveryoneUseCase: DeleteVisualizationForEveryoneUseCase,
    private val hideVisualizationForMeUseCase: HideVisualizationForMeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUIState())

    val uiState: StateFlow<FeedUIState> =
        _uiState.asStateFlow()

    private var allVisualizations: List<VisualizationCard> =
        emptyList()

    // TODO: Replace with Auth repository
    val currentUserID: String =
        "e9Nk8XrxHJAtwN3Hf2FL"

    init {
        loadData(forceRefresh = false)
    }

    // ─────────────────────────────────────────────────────────────
    // Data Loading
    // ─────────────────────────────────────────────────────────────

    fun loadData(forceRefresh: Boolean = false) {

        if (forceRefresh) {
            allVisualizations = emptyList()
        }

        if (allVisualizations.isEmpty()) {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
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

                        is AppError.NetworkError ->
                            "Connection error. Please check your internet."

                        is AppError.ParsingError ->
                            "There was a problem reading some visualizations."

                        is AppError.NotFound ->
                            "No visualizations found."

                        else ->
                            "An unexpected error occurred. Please try again."
                    }

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

    // ─────────────────────────────────────────────────────────────
    // Filter / Search
    // ─────────────────────────────────────────────────────────────

    fun onFilterChange(filter: VisualizationFilter) {

        if (_uiState.value.selectedFilter == filter) {
            return
        }

        _uiState.update {
            it.copy(selectedFilter = filter)
        }

        if (allVisualizations.isNotEmpty()) {
            applyLocalFilterAndSearch()
        } else {
            loadData()
        }
    }

    fun onSearchTextChange(newText: String) {

        _uiState.update {
            it.copy(searchText = newText)
        }

        applyLocalFilterAndSearch()
    }

    private fun applyLocalFilterAndSearch() {

        val filter = _uiState.value.selectedFilter
        val search = _uiState.value.searchText

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

        _uiState.update {
            it.copy(
                items = filtered,
                isLoading = false,
                errorMessage = null
            )
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Menu
    // ─────────────────────────────────────────────────────────────

    fun onOpenMenu(visualizationId: String) {

        _uiState.update {
            it.copy(openMenuForId = visualizationId)
        }
    }

    fun onDismissMenu() {

        _uiState.update {
            it.copy(openMenuForId = null)
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Delete Dialogs
    // ─────────────────────────────────────────────────────────────

    fun onRequestDeleteForMe(visualizationId: String) {

        _uiState.update {
            it.copy(
                openMenuForId = null,
                deleteDialogState =
                    DeleteDialogState.ShowDeleteForMe(
                        visualizationId
                    )
            )
        }
    }

    fun onRequestDeleteForEveryone(visualizationId: String) {

        _uiState.update {
            it.copy(
                openMenuForId = null,
                deleteDialogState =
                    DeleteDialogState.ShowDeleteForEveryone(
                        visualizationId
                    )
            )
        }
    }

    fun onDismissDeleteDialog() {

        _uiState.update {
            it.copy(
                deleteDialogState = DeleteDialogState.Hidden
            )
        }
    }

    fun onConfirmDeleteForMe(visualizationId: String) {

        _uiState.update {
            it.copy(
                deleteDialogState = DeleteDialogState.Hidden
            )
        }

        viewModelScope.launch {

            hideVisualizationForMeUseCase(
                currentUserID,
                visualizationId
            ).fold(

                onSuccess = {

                    allVisualizations =
                        allVisualizations.filter {
                            it.id != visualizationId
                        }

                    applyLocalFilterAndSearch()
                },

                onFailure = { e ->

                    _uiState.update {
                        it.copy(
                            errorMessage =
                                e.message
                                    ?: "Failed to hide visualization"
                        )
                    }
                }
            )
        }
    }

    fun onConfirmDeleteForEveryone(
        visualizationId: String
    ) {

        _uiState.update {
            it.copy(
                deleteDialogState = DeleteDialogState.Hidden
            )
        }

        viewModelScope.launch {

            deleteVisualizationForEveryoneUseCase(
                visualizationId
            ).fold(

                onSuccess = {

                    allVisualizations =
                        allVisualizations.filter {
                            it.id != visualizationId
                        }

                    applyLocalFilterAndSearch()
                },

                onFailure = { e ->

                    _uiState.update {
                        it.copy(
                            errorMessage =
                                e.message
                                    ?: "Failed to delete visualization"
                        )
                    }
                }
            )
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Share
    // ─────────────────────────────────────────────────────────────

    fun onShareSelected() {

        _uiState.update {
            it.copy(openMenuForId = null)
        }
    }
}

/**
 * Feed Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedPage(
    modifier: Modifier = Modifier,
    feedViewModel: FeedViewModel = hiltViewModel(),
    onVisualizationClick: (String) -> Unit = {}
) {

    val uiState by
    feedViewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(
            scrollBehavior.nestedScrollConnection
        ),

        topBar = {

            FeedTopBar(
                scrollBehavior = scrollBehavior,
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = {
                    feedViewModel.onFilterChange(it)
                }
            )
        }

    ) { paddingValues ->

        PullToRefreshBox(

            isRefreshing =
                uiState.isLoading &&
                    uiState.items.isNotEmpty(),

            onRefresh = {
                feedViewModel.loadData(forceRefresh = true)
            },

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding()
                )

        ) {

            when {

                uiState.isLoading &&
                    uiState.items.isEmpty() -> {

                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                uiState.errorMessage != null -> {

                    Text(
                        text = uiState.errorMessage!!,
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                uiState.items.isEmpty() &&
                    !uiState.isLoading -> {

                    Text(
                        text = "No visualizations found.",
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                else -> {

                    LazyColumn(

                        verticalArrangement =
                            Arrangement.spacedBy(12.dp),

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)

                    ) {

                        item {

                            Spacer(
                                modifier = Modifier.height(22.dp)
                            )

                            SearchSection(
                                text = uiState.searchText,
                                onTextChange = {
                                    feedViewModel
                                        .onSearchTextChange(it)
                                }
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )
                        }

                        items(
                            items = uiState.items,
                            key = { it.id }
                        ) { item ->

                            FeedCard(

                                item = item,

                                currentUserID =
                                    feedViewModel.currentUserID,

                                isMenuOpen =
                                    uiState.openMenuForId == item.id,

                                onClick = {
                                    onVisualizationClick(item.id)
                                },

                                onOpenMenu = {
                                    feedViewModel.onOpenMenu(item.id)
                                },

                                onDismissMenu = {
                                    feedViewModel.onDismissMenu()
                                },

                                onShare = {
                                    feedViewModel.onShareSelected()
                                },

                                onDeleteForMe = {
                                    feedViewModel
                                        .onRequestDeleteForMe(item.id)
                                },

                                onDeleteForEveryone = {
                                    feedViewModel
                                        .onRequestDeleteForEveryone(item.id)
                                }
                            )
                        }

                        item {
                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
