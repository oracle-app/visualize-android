package com.oracle.visualize.presentation.screens.feedScreen

import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter

data class FeedUIState(
    val items: List<VisualizationCard> = emptyList(),
    val searchText: String = "",
    val selectedFilter: VisualizationFilter = VisualizationFilter.ALL,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isSearching: Boolean = false

)
