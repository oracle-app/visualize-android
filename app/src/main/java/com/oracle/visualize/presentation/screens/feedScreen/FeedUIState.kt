package com.oracle.visualize.presentation.screens.feedScreen

import com.oracle.visualize.domain.models.VisualizationCard
import com.oracle.visualize.domain.models.enums.VisualizationFilter


data class FeedUIState(
    val items: List<VisualizationCard> = emptyList(),
    val searchText: String = "",
    val selectedFilter: VisualizationFilter = VisualizationFilter.ALL,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val openMenuForId: String? = null,
    val deleteDialogState: DeleteDialogState = DeleteDialogState.Hidden
)


sealed interface DeleteDialogState {

    object Hidden : DeleteDialogState


    data class ShowDeleteForMe(val visualizationId: String) : DeleteDialogState


    data class ShowDeleteForEveryone(val visualizationId: String) : DeleteDialogState
}
