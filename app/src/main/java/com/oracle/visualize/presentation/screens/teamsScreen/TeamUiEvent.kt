package com.oracle.visualize.presentation.screens.teamsScreen

/**
 * All possible UI events from the Teams screen.
 */
sealed interface TeamsUiEvent {
    data class ToggleExpand(val teamId: String) : TeamsUiEvent            // Teams I'm In
    data class SwipeTeam(val teamId: String?) : TeamsUiEvent
    // Opens the delete confirmation dialog
    data class RequestDeleteTeam(val teamId: String) : TeamsUiEvent
    // Confirmed by the user in the dialog → actually deletes
    data class ConfirmDeleteTeam(val teamId: String) : TeamsUiEvent
    // Dismissed by the user in the dialog → no-op
    object DismissDeleteDialog : TeamsUiEvent
    object NavigateToCreateTeam : TeamsUiEvent
    data class NavigateToEditTeam(val teamId: String) : TeamsUiEvent
    object Refresh : TeamsUiEvent
}
