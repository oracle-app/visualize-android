package com.oracle.visualize.presentation.screens.teamsScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.team.DeleteTeamUseCase
import com.oracle.visualize.domain.usecases.team.GetUsersTeamsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamsViewModel @Inject constructor(
    private val getUsersTeamsUseCase: GetUsersTeamsUseCase,
    private val deleteTeamUseCase: DeleteTeamUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()


    init { loadTeams() }

    private fun loadTeams() {

        val userID = authRepository.getCurrentUserID()

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
        if (event is TeamsUiEvent.Refresh) {
            loadTeams()
            return
        }

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

            is TeamsUiEvent.RequestDeleteTeam ->
                _uiState.value = current.copy(
                    swipedTeamId        = null,
                    teamPendingDeleteId = event.teamId
                )

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

            is TeamsUiEvent.DismissDeleteDialog ->
                _uiState.value = current.copy(teamPendingDeleteId = null)

            else -> Unit
        }
    }
}
