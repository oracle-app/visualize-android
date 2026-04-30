package com.oracle.visualize.presentation.screens.createEditScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.CreateTeamUseCase
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import com.oracle.visualize.domain.usecases.GetUsersTeamsUseCase
import com.oracle.visualize.domain.usecases.UpdateTeamUseCase
import com.oracle.visualize.presentation.screens.teamsScreen.TeamsMockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    // Real userID for persistence
    private val userID = "e9Nk8XrxHJAtwN3Hf2FL"

    init {
        loadInitialData()
        setupSearchDebounce()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CreateEditTeamUiState.Loading
            try {
                if (teamIdArg != null) {
                    // MockData usage (commented out)
                    // val allTeams = TeamsMockData.myTeams + TeamsMockData.teamsImIn
                    // val team = allTeams.find { it.id == teamIdArg }

                    // REAL USE CASE CALL (Firestore)
                    val allTeams = getUsersTeamsUseCase.getTeamsUserOwns(userID)
                    val team = allTeams.find { it.id == teamIdArg }
                    
                    _uiState.value = CreateEditTeamUiState.Content(
                        teamId = teamIdArg,
                        teamName = team?.name ?: "",
                        members = team?.members ?: emptyList(),
                        ownerID = userID,
                        suggestions = emptyList() // Mock: TeamsMockData.users.take(4)
                    )
                } else {
                    // Create mode
                    _uiState.value = CreateEditTeamUiState.Content(
                        ownerID = userID,
                        // Mock: members = listOf(TeamsMockData.users[0])
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CreateEditTeamUiState.Error(e.message ?: "Failed to load")
            }
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .collect { query ->
                    val current = _uiState.value as? CreateEditTeamUiState.Content ?: return@collect
                    if (query.isBlank()) {
                        _uiState.value = current.copy(searchResults = emptyList())
                        return@collect
                    }
                    
                    try {
                        // MockData usage (commented out)
                        /* 
                        val results = TeamsMockData.users.filter { 
                            it.email.contains(query, ignoreCase = true) || 
                            it.username.contains(query, ignoreCase = true)
                        }
                        */

                        // REAL USE CASE CALL
                        val results = getUserSuggestionsUseCase.invoke(query.lowercase().trim())
                        val filtered = results.filter { suggestion ->
                            current.members.none { it.id == suggestion.id }
                        }
                        _uiState.value = current.copy(searchResults = filtered)
                    } catch (e: Exception) {
                        // Log error
                    }
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
                if (event.user.id != current.ownerID) {
                    _uiState.value = current.copy(members = current.members - event.user)
                }
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
            try {
                val memberIDs = state.members.map { it.id }
                if (state.isEditMode) {
                    updateTeamUseCase.invoke(state.teamId!!, memberIDs, state.teamName.trim())
                } else {
                    // MockData usage (commented out)
                    // TeamsMockData.addTeam(state.teamName.trim(), state.members)

                    // REAL USE CASE CALL (Firestore)
                    createTeamUseCase.invoke(memberIDs, state.teamName.trim(), state.ownerID)
                }
                _uiState.value = CreateEditTeamUiState.Success
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isSubmitting = false,
                    nameError = e.message ?: "Failed to save team"
                )
            }
        }
    }
}