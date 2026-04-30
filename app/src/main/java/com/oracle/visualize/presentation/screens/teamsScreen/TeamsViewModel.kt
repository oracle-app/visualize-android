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

@HiltViewModel
class TeamsViewModel @Inject constructor(
    private val getUsersTeamsUseCase: GetUsersTeamsUseCase,
    private val deleteTeamUseCase: DeleteTeamUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeamsUiState>(TeamsUiState.Loading)
    val uiState: StateFlow<TeamsUiState> = _uiState.asStateFlow()

    // Real userID for persistence
    private val userID = "e9Nk8XrxHJAtwN3Hf2FL"

    init {
        loadTeams()
    }

    private fun loadTeams() {
        viewModelScope.launch {
            _uiState.value = TeamsUiState.Loading
            try {
                // MockData usage (commented out)
                // val myTeams = TeamsMockData.myTeams
                // val teamsImIn = TeamsMockData.teamsImIn

                // REAL USE CASE CALL (Firestore)
                val myTeams   = getUsersTeamsUseCase.getTeamsUserOwns(userID)
                val teamsImIn = getUsersTeamsUseCase.getTeamsUserIsIn(userID)
                
                _uiState.value = TeamsUiState.Content(
                    myTeams   = myTeams,
                    teamsImIn = teamsImIn
                )
            } catch (e: Exception) {
                _uiState.value = TeamsUiState.Error(e.message ?: "Failed to load teams")
            }
        }
    }

    fun onEvent(event: TeamsUiEvent) {
        val current = _uiState.value as? TeamsUiState.Content ?: return
        when (event) {
            is TeamsUiEvent.ToggleExpand -> {
                val updated = if (event.teamId in current.expandedTeamIds) {
                    current.expandedTeamIds - event.teamId
                } else {
                    current.expandedTeamIds + event.teamId
                }
                _uiState.value = current.copy(expandedTeamIds = updated)
            }
            is TeamsUiEvent.SwipeTeam ->
                _uiState.value = current.copy(swipedTeamId = event.teamId)

            is TeamsUiEvent.DeleteTeam -> {
                viewModelScope.launch {
                    try {
                        // MockData usage (commented out)
                        // val updatedMyTeams = current.myTeams.filter { it.id != event.teamId }
                        // _uiState.value = current.copy(myTeams = updatedMyTeams, swipedTeamId = null)

                        deleteTeamUseCase.invoke(event.teamId)
                        loadTeams()
                    } catch (e: Exception) {
                        _uiState.value = TeamsUiState.Error(e.message ?: "Failed to delete team")
                    }
                }
            }
            is TeamsUiEvent.Refresh -> loadTeams()
            else -> Unit
        }
    }
}
