package com.oracle.visualize.presentation.screens.shareScreen

import com.oracle.visualize.domain.models.ShareUser

/**
 * Represents all possible UI events from the Share and Post screen.
 * Replaces 9+ individual lambda parameters with a single onEvent callback,
 * decoupling the UI from the specific ViewModel implementation.
 */
sealed interface ShareUiEvent {
    data class EmailQueryChanged(val query: String) : ShareUiEvent
    data class AddUserByEmail(val email: String) : ShareUiEvent
    data class RemoveUser(val user: ShareUser) : ShareUiEvent
    data class ToggleTeam(val teamId: String, val isMyTeam: Boolean) : ShareUiEvent
    object ConfirmShare : ShareUiEvent
    object BackPressed : ShareUiEvent
    object DismissUnsavedChanges : ShareUiEvent
    object ConfirmLeave : ShareUiEvent
}