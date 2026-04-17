package com.oracle.visualize.presentation.screens.ShareScreen

import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.models.ShareAndPostState
import com.oracle.visualize.domain.models.ShareTeam
import com.oracle.visualize.domain.models.ShareUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ShareAndPostViewModel : ViewModel() {

    private val _state = MutableStateFlow(ShareAndPostState())
    val state: StateFlow<ShareAndPostState> = _state.asStateFlow()

    init {
        // TODO: Replace with real UseCase call when data layer is ready
        _state.update {
            it.copy(
                suggestedUsers = ShareMockData.suggestedUsers,
                myTeams = ShareMockData.myTeams,
                teamsImIn = ShareMockData.teamsImIn
            )
        }
    }

    // Replace with real data source (UseCase / Repository) when available
    fun loadData(myTeams: List<ShareTeam>, teamsImIn: List<ShareTeam>) {
        _state.update { it.copy(myTeams = myTeams, teamsImIn = teamsImIn) }
    }

    fun onEmailQueryChange(query: String) {
        _state.update { it.copy(emailQuery = query) }
    }

    fun onAddUserByEmail(email: String) {
        if (email.isBlank()) return
        val newUser = ShareUser(
            id = email,
            name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
            email = email,
            avatarInitials = email.first().uppercase()
        )
        _state.update {
            it.copy(selectedUsers = it.selectedUsers + newUser, emailQuery = "")
        }
    }

    fun onRemoveUser(user: ShareUser) {
        _state.update { state ->
            state.copy(
                selectedUsers = state.selectedUsers - user,
                suggestedUsers = state.suggestedUsers - user
            )
        }
    }

    fun onToggleTeam(teamId: String, isMyTeam: Boolean) {
        _state.update { state ->
            if (isMyTeam) {
                state.copy(myTeams = state.myTeams.map { team ->
                    if (team.id == teamId) team.copy(isSelected = !team.isSelected) else team
                })
            } else {
                state.copy(teamsImIn = state.teamsImIn.map { team ->
                    if (team.id == teamId) team.copy(isSelected = !team.isSelected) else team
                })
            }
        }
    }

    fun onConfirmShare() {
        // TODO: call UseCase to persist share action
    }

    fun onBackPressed() {
        val hasSelections = _state.value.selectedUsers.isNotEmpty() ||
                _state.value.myTeams.any { it.isSelected } ||
                _state.value.teamsImIn.any { it.isSelected }
        if (hasSelections) {
            _state.update { it.copy(showUnsavedChangesDialog = true) }
        }
    }

    fun onDismissUnsavedChanges() {
        _state.update { it.copy(showUnsavedChangesDialog = false) }
    }

    fun onConfirmLeave() {
        _state.update {
            it.copy(
                showUnsavedChangesDialog = false,
                selectedUsers = emptyList(),
                myTeams = it.myTeams.map { t -> t.copy(isSelected = false) },
                teamsImIn = it.teamsImIn.map { t -> t.copy(isSelected = false) }
            )
        }
    }
}