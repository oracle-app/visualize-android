package com.oracle.visualize.presentation.screens.teamsScreen

import com.oracle.visualize.domain.models.ShareUser

sealed interface CreateEditTeamUiState {

    object Loading : CreateEditTeamUiState

    data class Content(
        val teamId: String? = null,           // null = create mode, non-null = edit mode
        val teamName: String = "",
        val searchQuery: String = "",
        val searchResults: List<ShareUser> = emptyList(),
        val members: List<ShareUser> = emptyList(),   // current team members
        val suggestions: List<ShareUser> = emptyList(),
        val ownerID: String = "",
        val isSubmitting: Boolean = false,
        val nameError: String? = null
    ) : CreateEditTeamUiState {
        val isEditMode: Boolean get() = teamId != null
        val canSubmit: Boolean get() = teamName.isNotBlank() && members.isNotEmpty() && !isSubmitting
    }

    data class Error(val message: String) : CreateEditTeamUiState
    object Success : CreateEditTeamUiState
}