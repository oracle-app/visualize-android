package com.oracle.visualize.presentation.screens.shareScreen

import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser

data class ShareAndPostState(
    val emailQuery: String = "",
    val selectedUsers: List<ShareUser> = emptyList(),
    val suggestedUsers: List<ShareUser> = emptyList(),
    val myTeams: List<ShareTeam> = emptyList(),
    val teamsImIn: List<ShareTeam> = emptyList(),
    val showUnsavedChangesDialog: Boolean = false
)