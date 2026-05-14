package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen

import com.oracle.visualize.domain.models.ShareUser

/**
 * Represents the UI state for the "Share to More Teammates" screen.
 * This screen allows the owner of a visualization to view already-shared users
 * and add or remove teammates.
 */
sealed interface ShareWithTeammatesUiState {

    object Loading : ShareWithTeammatesUiState

    data class Content(
        val visualizationId: String,
        val sharedUsers: List<ShareUser>,
        val emailQuery: String = "",
        val suggestedUsers: List<ShareUser> = emptyList(),
        val removeDialogForUser: ShareUser? = null,
        val isSubmitting: Boolean = false
    ) : ShareWithTeammatesUiState

    data class Error(val message: String) : ShareWithTeammatesUiState
}
