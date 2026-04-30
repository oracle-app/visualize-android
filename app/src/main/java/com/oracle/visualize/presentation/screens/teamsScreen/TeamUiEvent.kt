package com.oracle.visualize.presentation.screens.teamsScreen

/**
 * All possible UI events from the Teams screen.
 */
sealed interface TeamsUiEvent {
    data class ToggleExpand(val teamId: String) : TeamsUiEvent
    data class SwipeTeam(val teamId: String?) : TeamsUiEvent
    data class DeleteTeam(val teamId: String) : TeamsUiEvent
    object NavigateToCreateTeam : TeamsUiEvent
    data class NavigateToEditTeam(val teamId: String) : TeamsUiEvent
    object Refresh : TeamsUiEvent
}