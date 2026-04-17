package com.oracle.visualize.domain.models

data class ShareAndPostState(
    val emailQuery: String = "",
    val selectedUsers: List<ShareUser> = emptyList(),
    val suggestedUsers: List<ShareUser> = emptyList(),
    val myTeams: List<ShareTeam> = emptyList(),
    val teamsImIn: List<ShareTeam> = emptyList(),
    val showUnsavedChangesDialog: Boolean = false
)