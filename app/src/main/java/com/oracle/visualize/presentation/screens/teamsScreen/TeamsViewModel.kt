package com.oracle.visualize.presentation.screens.teamsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.DeleteTeamUseCase
import com.oracle.visualize.domain.usecases.GetUsersTeamsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Teams screen.
 */
@HiltViewModel
class TeamsViewModel @Inject constructor(
    private val getUsersTeamsUseCase: GetUsersTeamsUseCase,
    private val deleteTeamUseCase: DeleteTeamUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()

    // TODO: Replace with actual session user ID from AuthRepository
    private val userID = "e9Nk8XrxHJAtwN3Hf2FL"

    init { loadTeams() }

    private fun loadTeams() {
        viewModelScope.launch {
            _uiState.value = TeamsUiState.Loading

            val myTeamsResult   = getUsersTeamsUseCase.getTeamsUserOwns(userID)
            val teamsImInResult = getUsersTeamsUseCase.getTeamsUserIsIn(userID)

            if (myTeamsResult.isFailure) {
                _uiState.value = TeamsUiState.Error(
                    myTeamsResult.exceptionOrNull()?.message ?: "Failed to load teams"
                )
                return@launch
            }
            if (teamsImInResult.isFailure) {
                _uiState.value = TeamsUiState.Error(
                    teamsImInResult.exceptionOrNull()?.message ?: "Failed to load teams"
                )
                return@launch
            }

            _uiState.value = TeamsUiState.Content(
                myTeams   = myTeamsResult.getOrDefault(emptyList()),
                teamsImIn = teamsImInResult.getOrDefault(emptyList())
            )
        }
    }

    fun onEvent(event: TeamsUiEvent) {
        val current = _uiState.value as? TeamsUiState.Content ?: return
        when (event) {
            is TeamsUiEvent.ToggleExpand -> {
                val updated = if (event.teamId in current.expandedTeamIds)
                    current.expandedTeamIds - event.teamId
                else
                    current.expandedTeamIds + event.teamId
                _uiState.value = current.copy(expandedTeamIds = updated)
            }

            is TeamsUiEvent.SwipeTeam ->
                _uiState.value = current.copy(swipedTeamId = event.teamId)

            // Step 1 — show the confirmation dialog
            is TeamsUiEvent.RequestDeleteTeam ->
                _uiState.value = current.copy(
                    swipedTeamId       = null,
                    teamPendingDeleteId = event.teamId
                )

            // Step 2 — user confirmed, perform the delete
            is TeamsUiEvent.ConfirmDeleteTeam -> {
                _uiState.value = current.copy(teamPendingDeleteId = null)
                viewModelScope.launch {
                    deleteTeamUseCase(event.teamId).fold(
                        onSuccess = { loadTeams() },
                        onFailure = { e ->
                            _uiState.value = TeamsUiState.Error(
                                e.message ?: "Failed to delete team"
                            )
                        }
                    )
                }
            }

            // User cancelled — just close the dialog
            is TeamsUiEvent.DismissDeleteDialog ->
                _uiState.value = current.copy(teamPendingDeleteId = null)

            is TeamsUiEvent.Refresh -> loadTeams()

            // Navigation events are handled in the View layer
            else -> Unit
        }
    }
}
