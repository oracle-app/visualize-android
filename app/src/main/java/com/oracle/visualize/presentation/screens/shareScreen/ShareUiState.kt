package com.oracle.visualize.presentation.screens.shareScreen

import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser

/**
 * Represents the UI state of the Share and Post screen.
 * Moved to presentation layer — UI state has no concern with business logic.
 * Follows the sealed interface pattern established by CreateUiState.
 */
sealed interface ShareUiState {

    object Loading : ShareUiState

    data class Content(
        val emailQuery: String = "",
        val selectedUsers: List<ShareUser> = emptyList(),
        val suggestedUsers: List<ShareUser> = emptyList(),
        val myTeams: List<ShareTeam> = emptyList(),
        val teamsImIn: List<ShareTeam> = emptyList(),
        val selectedTeamIds: Set<String> = emptySet(),
        val hasChanges: Boolean = false,
        val showUnsavedChangesDialog: Boolean = false
    ) : ShareUiState

    data class Error(val message: String) : ShareUiState
}