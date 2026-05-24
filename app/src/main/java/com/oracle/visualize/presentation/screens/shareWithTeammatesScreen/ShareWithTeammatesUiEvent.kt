package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen

import com.oracle.visualize.domain.models.ShareUser

sealed interface ShareWithTeammatesUiEvent {
    // Users
    data class EmailQueryChanged(val query: String) : ShareWithTeammatesUiEvent
    data class SelectSuggestion(val user: ShareUser) : ShareWithTeammatesUiEvent
    data class RequestRemoveUser(val user: ShareUser) : ShareWithTeammatesUiEvent
    object DismissRemoveDialog : ShareWithTeammatesUiEvent
    data class ConfirmRemoveUser(val user: ShareUser) : ShareWithTeammatesUiEvent
    // Teams
    data class ToggleTeam(val teamId: String) : ShareWithTeammatesUiEvent
    // Actions
    object ConfirmShare : ShareWithTeammatesUiEvent
    object BackPressed : ShareWithTeammatesUiEvent
}
