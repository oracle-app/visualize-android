package com.oracle.visualize.presentation.screens.feedScreen

import com.oracle.visualize.domain.models.FeedItem
import com.oracle.visualize.domain.models.enums.VisualizationFilter

sealed interface FeedUiState {

    object Loading : FeedUiState

    data class Error(val message: Int) : FeedUiState

    data class Success(
        val items: List<FeedItem>,
        val searchText: String = "",
        val selectedFilter: VisualizationFilter = VisualizationFilter.ALL,
        val isRefreshing: Boolean = false,
        val isSearching: Boolean = false
    ) : FeedUiState
}
