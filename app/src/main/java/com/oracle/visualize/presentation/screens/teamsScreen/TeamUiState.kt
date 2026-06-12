package com.oracle.visualize.presentation.screens.teamsScreen

import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.enums.UserType

/**
 * Represents the UI state of the Teams screen.
 */
sealed interface TeamsUiState {

    object Loading : TeamsUiState

    data class Content(
        val myTeams: List<ShareTeam> = emptyList(),
        val teamsImIn: List<ShareTeam> = emptyList(),
        val expandedTeamIds: Set<String> = emptySet(),
        val swipedTeamId: String? = null,
        val teamPendingDeleteId: String? = null,
        val userType: UserType = UserType.CONSUMER
    ) : TeamsUiState

    data class Error(val message: Int) : TeamsUiState
}
