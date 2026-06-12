package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen

import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser

sealed interface ShareWithTeammatesUiState {

    object Loading : ShareWithTeammatesUiState

    data class Error(val message: Int) : ShareWithTeammatesUiState

    data class Content(
        val visualizationId: String,
        // Individual users
        val sharedUsers: List<ShareUser> = emptyList(),
        val emailQuery: String = "",
        val suggestedUsers: List<ShareUser> = emptyList(),
        val removeDialogForUser: ShareUser? = null,
        // Teams
        val myTeams: List<ShareTeam> = emptyList(),
        val teamsImIn: List<ShareTeam> = emptyList(),
        val selectedTeamIds: Set<String> = emptySet(),
        // Submit
        val isSubmitting: Boolean = false,
        val shareSuccess: Boolean = false,
        val errorMessage: Int? = null
    ) : ShareWithTeammatesUiState
}
