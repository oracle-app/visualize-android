package com.oracle.visualize.presentation.screens.feedScreen

import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter

data class FeedUIState(
    val items: List<VisualizationCard> = emptyList(),
    val searchText: String = "",
    val selectedFilter: VisualizationFilter = VisualizationFilter.ALL,
    val isLoading: Boolean = true, // Extra: agregamos loading
    val errorMessage: String? = null // Extra: agregamos manejo de error
)
