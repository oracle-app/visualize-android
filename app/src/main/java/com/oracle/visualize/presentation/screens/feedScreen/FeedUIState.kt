package com.oracle.visualize.presentation.screens.feedScreen

import com.oracle.visualize.domain.models.FeedItem
import com.oracle.visualize.domain.models.enums.VisualizationFilter
import com.oracle.visualize.ui.theme.ChartPalette

sealed interface FeedUiState {

    object Loading : FeedUiState

    data class Error(val message: Int) : FeedUiState

    data class Success(
        val items: List<FeedItem>,
        val currentUserID: String = "",
        val searchText: String = "",
        val selectedFilter: VisualizationFilter = VisualizationFilter.ALL,
        val isRefreshing: Boolean = false,
        val isSearching: Boolean = false,
        val menuOpenForId: String? = null,
        val deleteDialogForId: String? = null,
        val hideDialogForId: String? = null,
        val isDeletableMap: Map<String, Boolean> = emptyMap(),
        val pendingShareId: String? = null,
        val chartColorTheme: ChartPalette = ChartPalette.THEME1
    ) : FeedUiState
}
