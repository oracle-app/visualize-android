package com.oracle.visualize.presentation.screens.teamsScreen

import com.oracle.visualize.domain.models.ShareTeam

/**
 * Represents the UI state of the Teams screen.
 */
sealed interface TeamsUiState {

    object Loading : TeamsUiState

    data class Content(
        val myTeams: List<ShareTeam> = emptyList(),
        val teamsImIn: List<ShareTeam> = emptyList(),
        val expandedTeamIds: Set<String> = emptySet(),       // Teams I'm In expanded rows
        val swipedTeamId: String? = null,
        // Non-null while the delete confirmation dialog is visible
        val teamPendingDeleteId: String? = null
    ) : TeamsUiState

    data class Error(val message: Int) : TeamsUiState
}
