package com.oracle.visualize.presentation.screens.ShareScreen

import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ShareAndPostViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Loading)
    val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()

    fun loadData(
        myTeams: List<ShareTeam>,
        teamsImIn: List<ShareTeam>,
        suggestedUsers: List<ShareUser> = emptyList()
    ) {
        _uiState.value = ShareUiState.Content(
            myTeams = myTeams,
            teamsImIn = teamsImIn,
            suggestedUsers = suggestedUsers
        )
    }

    fun onEvent(event: ShareUiEvent) {
        val current = _uiState.value as? ShareUiState.Content ?: return
        _uiState.value = when (event) {

            is ShareUiEvent.EmailQueryChanged ->
                current.copy(emailQuery = event.query)

            is ShareUiEvent.AddUserByEmail -> {
                if (event.email.isBlank()) return
                val newUser = ShareUser(
                    id = event.email,
                    name = event.email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    email = event.email,
                    avatarInitials = event.email.first().uppercase()
                )
                val updated = current.copy(
                    selectedUsers = current.selectedUsers + newUser,
                    emailQuery = ""
                )
                updated.copy(hasChanges = computeHasChanges(updated))
            }

            is ShareUiEvent.RemoveUser -> {
                val updated = current.copy(
                    selectedUsers = current.selectedUsers - event.user,
                    suggestedUsers = current.suggestedUsers - event.user
                )
                updated.copy(hasChanges = computeHasChanges(updated))
            }

            is ShareUiEvent.ToggleTeam -> {
                val updated = if (event.teamId in current.selectedTeamIds) {
                    current.copy(selectedTeamIds = current.selectedTeamIds - event.teamId)
                } else {
                    current.copy(selectedTeamIds = current.selectedTeamIds + event.teamId)
                }
                updated.copy(hasChanges = computeHasChanges(updated))
            }

            is ShareUiEvent.BackPressed -> {
                if (current.hasChanges) {
                    current.copy(showUnsavedChangesDialog = true)
                } else {
                    current
                }
            }

            is ShareUiEvent.DismissUnsavedChanges ->
                current.copy(showUnsavedChangesDialog = false)

            is ShareUiEvent.ConfirmLeave ->
                current.copy(
                    showUnsavedChangesDialog = false,
                    selectedUsers = emptyList(),
                    selectedTeamIds = emptySet(),
                    hasChanges = false
                )

            is ShareUiEvent.ConfirmShare -> {
                current
            }
        }
    }

    private fun computeHasChanges(state: ShareUiState.Content): Boolean =
        state.selectedUsers.isNotEmpty() || state.selectedTeamIds.isNotEmpty()
}