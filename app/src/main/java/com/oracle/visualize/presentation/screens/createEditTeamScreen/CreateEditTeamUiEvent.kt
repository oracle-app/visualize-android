package com.oracle.visualize.presentation.screens.createEditTeamScreen

import com.oracle.visualize.domain.models.ShareUser

sealed interface CreateEditTeamUiEvent {
    data class NameChanged(val name: String) : CreateEditTeamUiEvent
    data class SearchQueryChanged(val query: String) : CreateEditTeamUiEvent
    data class AddMember(val user: ShareUser) : CreateEditTeamUiEvent
    data class RemoveMember(val user: ShareUser) : CreateEditTeamUiEvent
    object Submit : CreateEditTeamUiEvent
    // Back button tapped — show dialog if there are unsaved changes
    object RequestBack : CreateEditTeamUiEvent
    // User confirmed leaving despite unsaved changes
    object ConfirmDiscard : CreateEditTeamUiEvent
    // User chose to stay on the screen
    object DismissUnsavedChangesDialog : CreateEditTeamUiEvent
}
