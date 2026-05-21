package com.oracle.visualize.presentation.screens.createEditScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.CreateTeamUseCase
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import com.oracle.visualize.domain.usecases.GetUsersTeamsUseCase
import com.oracle.visualize.domain.usecases.UpdateTeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Create/Edit Team screen.
 *
 * Owner visibility contract:
 *   The owner is always the first entry in [CreateEditTeamUiState.Content.members].
 *   This guarantees it appears at the top of the Member List with the "owner" label.
 *   On submit, [state.members] already contains the owner so the full membersIDs
 *   list (owner + added members) is sent to the use case as-is.
 */
@HiltViewModel
class CreateEditTeamViewModel @Inject constructor(
    private val createTeamUseCase: CreateTeamUseCase,
    private val updateTeamUseCase: UpdateTeamUseCase,
    private val getUserSuggestionsUseCase: GetUserSuggestionsUseCase,
    private val getUsersTeamsUseCase: GetUsersTeamsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateEditTeamUiState>(CreateEditTeamUiState.Loading)
    val uiState: StateFlow<CreateEditTeamUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val teamIdArg: String? = savedStateHandle["teamId"]

    // TODO: Replace with actual session user ID from AuthRepository
    private val userID = "e9Nk8XrxHJAtwN3Hf2FL"

    init {
        loadInitialData()
        setupSearchDebounce()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CreateEditTeamUiState.Loading

            if (teamIdArg != null) {
                // ── Edit mode ────────────────────────────────────────────────
                getUsersTeamsUseCase.getTeamsUserOwns(userID).fold(
                    onSuccess = { teams ->
                        val team = teams.find { it.id == teamIdArg }
                        // members already includes the owner because TeamDatasource
                        // stores ownerID inside membersIDs
                        _uiState.value = CreateEditTeamUiState.Content(
                            teamId      = teamIdArg,
                            teamName    = team?.name ?: "",
                            members     = team?.members ?: emptyList(),
                            ownerID     = userID,
                            suggestions = emptyList()
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = CreateEditTeamUiState.Error(
                            e.message ?: "Failed to load team"
                        )
                    }
                )
            } else {
                // ── Create mode ──────────────────────────────────────────────
                // Fetch owner's ShareUser so we can show them at the top of
                // the Member List immediately, before any members are added.
                val ownedDeferred  = async { getUsersTeamsUseCase.getTeamsUserOwns(userID) }
                val memberDeferred = async { getUsersTeamsUseCase.getTeamsUserIsIn(userID) }

                val ownedTeams  = ownedDeferred.await().getOrNull()  ?: emptyList()
                val memberTeams = memberDeferred.await().getOrNull() ?: emptyList()

                // Resolve the owner's ShareUser from any team where they appear as a member
                val ownerAsUser = (ownedTeams + memberTeams)
                    .flatMap { it.members }
                    .firstOrNull { it.id == userID }

                // Suggestions: teammates the owner has worked with, excluding themselves
                val suggestions = (ownedTeams + memberTeams)
                    .flatMap { it.members }
                    .filter    { it.id != userID }
                    .distinctBy { it.id }

                _uiState.value = CreateEditTeamUiState.Content(
                    ownerID     = userID,
                    // Seed the member list with the owner so they always appear at the top
                    members     = listOfNotNull(ownerAsUser),
                    suggestions = suggestions
                )
            }
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery.debounce(500).collect { query ->
                val current = _uiState.value as? CreateEditTeamUiState.Content ?: return@collect
                if (query.isBlank()) {
                    _uiState.value = current.copy(searchResults = emptyList())
                    return@collect
                }
                getUserSuggestionsUseCase(query.lowercase().trim()).fold(
                    onSuccess = { results ->
                        _uiState.value = current.copy(
                            searchResults = results.filter { r ->
                                current.members.none { it.id == r.id }
                            }
                        )
                    },
                    onFailure = {}
                )
            }
        }
    }

    fun onEvent(event: CreateEditTeamUiEvent) {
        val current = _uiState.value as? CreateEditTeamUiState.Content ?: return
        when (event) {
            is CreateEditTeamUiEvent.NameChanged ->
                _uiState.value = current.copy(teamName = event.name, nameError = null)

            is CreateEditTeamUiEvent.SearchQueryChanged -> {
                _searchQuery.value = event.query
                _uiState.value = current.copy(searchQuery = event.query)
            }

            is CreateEditTeamUiEvent.AddMember -> {
                if (current.members.any { it.id == event.user.id }) return
                _uiState.value = current.copy(
                    members       = current.members + event.user,
                    searchQuery   = "",
                    searchResults = emptyList(),
                    suggestions   = current.suggestions.filter { it.id != event.user.id }
                )
                _searchQuery.value = ""
            }

            is CreateEditTeamUiEvent.RemoveMember -> {
                // Owner cannot be removed
                if (event.user.id != current.ownerID)
                    _uiState.value = current.copy(members = current.members - event.user)
            }

            is CreateEditTeamUiEvent.Submit -> submitTeam(current)
        }
    }

    private fun submitTeam(state: CreateEditTeamUiState.Content) {
        if (state.teamName.isBlank()) {
            _uiState.value = state.copy(nameError = "Team name cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true)
            // state.members already includes the owner, so memberIDs is the complete list
            val memberIDs = state.members.map { it.id }
            val result = if (state.isEditMode)
                updateTeamUseCase(state.teamId!!, memberIDs, state.teamName.trim())
            else
                createTeamUseCase(memberIDs, state.teamName.trim(), state.ownerID)

            result.fold(
                onSuccess = { _uiState.value = CreateEditTeamUiState.Success },
                onFailure = { e ->
                    _uiState.value = state.copy(
                        isSubmitting = false,
                        nameError    = e.message ?: "Failed to save team"
                    )
                }
            )
        }
    }
}
