package com.oracle.visualize.presentation.screens.teamsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
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

    private val userID: String = authRepository.getCurrentUserID() ?: ""

    init {
        if (userID.isBlank()) {
            _uiState.value = TeamsUiState.Error(R.string.error_unknown_retry)
        } else {
            loadTeams()
        }
    }


    private fun loadTeams() {
        viewModelScope.launch {
            _uiState.value = TeamsUiState.Loading

            val myTeamsResult   = getUsersTeamsUseCase.getTeamsUserOwns(userID)
            val teamsImInResult = getUsersTeamsUseCase.getTeamsUserIsIn(userID)

            if (myTeamsResult is AppResult.Success && teamsImInResult is AppResult.Success) {
                _uiState.value = TeamsUiState.Content(
                    myTeams = myTeamsResult.data,
                    teamsImIn = teamsImInResult.data
                )
            } else {
                val error = (myTeamsResult as? AppResult.Error)?.error
                    ?: (teamsImInResult as? AppResult.Error)?.error

                val errorId = when (error) {
                    is AppError.NetworkError -> R.string.error_network
                    else -> R.string.error_teams_load_failed
                }
                _uiState.value = TeamsUiState.Error(
                    errorId
                )
            }
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

            is TeamsUiEvent.RequestDeleteTeam ->
                _uiState.value = current.copy(
                    swipedTeamId        = null,
                    teamPendingDeleteId = event.teamId
                )

            is TeamsUiEvent.ConfirmDeleteTeam -> {
                _uiState.value = current.copy(teamPendingDeleteId = null)
                viewModelScope.launch {
                    when (val result = deleteTeamUseCase(event.teamId)) {
                        is AppResult.Success -> {
                            loadTeams()
                        }
                        is AppResult.Error -> {
                            val errorId = when (result.error) {
                                is AppError.NetworkError -> R.string.error_network
                                else -> R.string.error_team_delete_failed
                            }
                            _uiState.value = TeamsUiState.Error(
                                errorId
                            )
                        }
                    }
                }
            }

            is TeamsUiEvent.DismissDeleteDialog ->
                _uiState.value = current.copy(teamPendingDeleteId = null)

            is TeamsUiEvent.Refresh -> loadTeams()

            else -> Unit
        }
    }
}
