package com.oracle.visualize.presentation.screens.createEditScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.CreateTeamUseCase
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import com.oracle.visualize.domain.usecases.GetUsersTeamsUseCase
import com.oracle.visualize.domain.usecases.UpdateTeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Create/Edit Team screen.
 *
 * @property createTeamUseCase Use case for creating a new team.
 * @property updateTeamUseCase Use case for updating an existing team.
 * @property getUserSuggestionsUseCase Use case for searching users by email.
 * @property getUsersTeamsUseCase Use case for fetching teams to pre-populate edit mode.
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
    private val userID = "e9Nk8XrxHJAtwN3Hf2FL"

    init {
        loadInitialData()
        setupSearchDebounce()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CreateEditTeamUiState.Loading
            if (teamIdArg != null) {
                getUsersTeamsUseCase.getTeamsUserOwns(userID).fold(
                    onSuccess = { teams ->
                        val team = teams.find { it.id == teamIdArg }
                        _uiState.value = CreateEditTeamUiState.Content(
                            teamId      = teamIdArg,
                            teamName    = team?.name ?: "",
                            members     = team?.members ?: emptyList(),
                            ownerID     = userID,
                            suggestions = emptyList()
                        )
                    },
                    onFailure = { e ->
                        _uiState.value = CreateEditTeamUiState.Error(e.message ?: "Failed to load team")
                    }
                )
            } else {
                _uiState.value = CreateEditTeamUiState.Content(ownerID = userID)
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
                    onSuccess = { suggestions ->
                        _uiState.value = current.copy(
                            searchResults = suggestions.filter { s -> current.members.none { it.id == s.id } }
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
                _uiState.value = current.copy(
                    members = current.members + event.user,
                    searchQuery = "",
                    searchResults = emptyList()
                )
                _searchQuery.value = ""
            }
            is CreateEditTeamUiEvent.RemoveMember -> {
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
                        nameError = e.message ?: "Failed to save team"
                    )
                }
            )
        }
    }
}
