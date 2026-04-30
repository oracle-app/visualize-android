package com.oracle.visualize.presentation.screens.teamsScreen

import com.oracle.visualize.domain.models.ShareTeam

/**
 * Represents the UI state of the Teams screen.
 * Follows the sealed interface pattern established in the project.
 */
sealed interface TeamsUiState {

    object Loading : TeamsUiState

    data class Content(
        val myTeams: List<ShareTeam> = emptyList(),
        val teamsImIn: List<ShareTeam> = emptyList(),
        // Which "Teams I'm In" items are expanded to show members
        val expandedTeamIds: Set<String> = emptySet(),
        // Which "My Teams" item is in swipe-to-reveal state
        val swipedTeamId: String? = null
    ) : TeamsUiState

    data class Error(val message: String) : TeamsUiState
}