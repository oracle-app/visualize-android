package com.oracle.visualize.presentation.screens.createEditScreen

import com.oracle.visualize.domain.models.ShareUser

sealed interface CreateEditTeamUiState {

    object Loading : CreateEditTeamUiState

    data class Content(
        val teamId: String? = null,
        val teamName: String = "",
        val searchQuery: String = "",
        val searchResults: List<ShareUser> = emptyList(),
        val members: List<ShareUser> = emptyList(),
        val suggestions: List<ShareUser> = emptyList(),
        val ownerID: String = "",
        val isSubmitting: Boolean = false,
        val nameError: String? = null,
        // True while the "Unsaved Changes" dialog is visible
        val showUnsavedChangesDialog: Boolean = false
    ) : CreateEditTeamUiState {
        val isEditMode: Boolean get() = teamId != null
        val canSubmit: Boolean get() = teamName.isNotBlank() && members.isNotEmpty() && !isSubmitting
        /** True if the user has made any change worth confirming before leaving. */
        val hasUnsavedChanges: Boolean get() =
            teamName.isNotBlank() || members.size > 1
    }

    data class Error(val message: String) : CreateEditTeamUiState
    object Success : CreateEditTeamUiState
}
