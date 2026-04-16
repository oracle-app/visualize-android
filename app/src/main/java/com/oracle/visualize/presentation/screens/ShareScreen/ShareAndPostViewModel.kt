package com.oracle.visualize.presentation.screens.ShareScreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ── Models ──────────────────────────────────────────
data class ShareUser(
    val id: String,
    val name: String,
    val email: String,
    val avatarInitials: String,
    val avatarColor: Long = 0xFFE8A87C
)

data class ShareTeam(
    val id: String,
    val name: String,
    val memberCount: Int,
    val members: List<ShareUser> = emptyList(),
    val isSelected: Boolean = false
)

data class ShareAndPostState(
    val emailQuery: String = "",
    val selectedUsers: List<ShareUser> = emptyList(),
    val suggestedUsers: List<ShareUser> = emptyList(),
    val myTeams: List<ShareTeam> = emptyList(),
    val teamsImIn: List<ShareTeam> = emptyList(),
    val showUnsavedChangesDialog: Boolean = false
)

class ShareAndPostViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        ShareAndPostState(
            suggestedUsers = listOf(
                ShareUser("1", "Diana Escalante", "dianaescalante@gmail.com", "D", 0xFFE8A87C),
                ShareUser("2", "Jocelyn Duarte",  "jocelynduarte@gmail.com",  "J", 0xFF7EC8C8),
                ShareUser("3", "Eduardo Salazar", "eduardosalazar@gmail.com", "E", 0xFF8CB87C)
            ),

            myTeams = listOf(
                ShareTeam(
                    id = "team1",
                    name = "Data Analyst",
                    memberCount = 2,
                    members = listOf(
                        ShareUser("u1", "Ana", "ana@gmail.com", "A", 0xFFE8A87C),
                        ShareUser("u2", "Bob", "bob@gmail.com", "B", 0xFF7EC8C8)
                    )
                ),
                ShareTeam(
                    id = "team2",
                    name = "Data Analyst",
                    memberCount = 5,
                    members = listOf(
                        ShareUser("u1", "Ana", "ana@gmail.com", "A", 0xFFE8A87C),
                        ShareUser("u2", "Bob", "bob@gmail.com", "B", 0xFF7EC8C8),
                        ShareUser("u3", "Carl", "carl@gmail.com", "C", 0xFFB8D4E8)
                    )
                )
            ),
            teamsImIn = listOf(
                ShareTeam(
                    id = "team3",
                    name = "Data Analyst",
                    memberCount = 5,
                    members = listOf(
                        ShareUser("u4", "Diana", "diana@gmail.com", "D", 0xFFE8C87C),
                        ShareUser("u5", "Eve", "eve@gmail.com", "E", 0xFF8CB87C)
                    )
                ),
                ShareTeam(
                    id = "team4",
                    name = "Data Analyst",
                    memberCount = 5,
                    members = listOf(
                        ShareUser("u4", "Diana", "diana@gmail.com", "D", 0xFFE8C87C),
                        ShareUser("u5", "Eve", "eve@gmail.com", "E", 0xFF8CB87C),
                        ShareUser("u6", "Frank", "frank@gmail.com", "F", 0xFFB87C8C)
                    )
                )
            )
        )
    )
    val state: StateFlow<ShareAndPostState> = _state.asStateFlow()

    fun onEmailQueryChange(query: String) {
        _state.update { it.copy(emailQuery = query) }
    }

    fun onRemoveUser(user: ShareUser) {
        _state.update { state ->
            val newSelected  = state.selectedUsers - user
            val newSuggested = state.suggestedUsers - user
            state.copy(
                selectedUsers  = newSelected,
                suggestedUsers = newSuggested
            )
        }
    }

    fun onAddUserByEmail(email: String) {
        if (email.isNotBlank()) {
            val newUser = ShareUser(
                id = email,
                name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = email,
                avatarInitials = email.first().uppercase()
            )
            _state.update {
                it.copy(
                    selectedUsers = it.selectedUsers + newUser,
                    emailQuery = ""
                )
            }
        }
    }

    fun onToggleTeam(teamId: String, isMyTeam: Boolean) {
        if (isMyTeam) {
            _state.update { state ->
                state.copy(
                    myTeams = state.myTeams.map { team ->
                        if (team.id == teamId) team.copy(isSelected = !team.isSelected) else team
                    }
                )
            }
        } else {
            _state.update { state ->
                state.copy(
                    teamsImIn = state.teamsImIn.map { team ->
                        if (team.id == teamId) team.copy(isSelected = !team.isSelected) else team
                    }
                )
            }
        }
    }

    fun onConfirmShare() {
        // Handle share confirmation
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