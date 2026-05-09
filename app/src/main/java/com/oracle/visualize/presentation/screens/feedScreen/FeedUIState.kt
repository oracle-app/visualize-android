package com.oracle.visualize.presentation.screens.feedScreen

import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter

/*data class FeedUIState(
    val items: List<VisualizationCard> = emptyList(),
    val searchText: String = "",
    val selectedFilter: VisualizationFilter = VisualizationFilter.ALL,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)*/

sealed interface FeedUiState {
    object Loading : FeedUiState
    data class Error(val message: String) : FeedUiState
    data class Success(
        val items: List<VisualizationCard>,
        val searchText: String = "",
        val selectedFilter: VisualizationFilter = VisualizationFilter.ALL,
    ) : FeedUiState
}
