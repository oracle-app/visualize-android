package com.oracle.visualize.presentation.screens.teamsScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.usecases.CreateTeamUseCase
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import com.oracle.visualize.domain.usecases.GetUsersTeamsUseCase
import com.oracle.visualize.domain.usecases.UpdateTeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    // teamId arg: null = create mode, non-null = edit mode
    private val teamIdArg: String? = savedStateHandle["teamId"]

    // Hardcoded User ID for testing — matching TeamsMockData
    private val userID = "user1"

    init {
        loadInitialData()
        setupSearchDebounce()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CreateEditTeamUiState.Loading
            delay(500) // Simulate delay
            try {
                if (teamIdArg != null) {
                    // Edit mode — load existing team from Mock Data
                    val allTeams = TeamsMockData.myTeams + TeamsMockData.teamsImIn
                    val team = allTeams.find { it.id == teamIdArg }
                    
                    _uiState.value = CreateEditTeamUiState.Content(
                        teamId = teamIdArg,
                        teamName = team?.name ?: "",
                        members = team?.members ?: emptyList(),
                        ownerID = userID,
                        suggestions = TeamsMockData.users.take(4)
                    )
                } else {
                    // Create mode
                    _uiState.value = CreateEditTeamUiState.Content(
                        ownerID = userID,
                        members = listOf(TeamsMockData.users[0]), // Diana as owner
                        suggestions = TeamsMockData.users.take(4)
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
                .debounce(300)
                .collect { query ->
                    val current = _uiState.value as? CreateEditTeamUiState.Content ?: return@collect
                    if (query.isBlank()) {
                        _uiState.value = current.copy(searchResults = emptyList())
                        return@collect
                    }
                    
                    val results = TeamsMockData.users.filter { 
                        it.email.contains(query, ignoreCase = true) || 
                        it.username.contains(query, ignoreCase = true)
                    }
                    
                    val filtered = results.filter { suggestion ->
                        current.members.none { it.id == suggestion.id }
                    }
                    _uiState.value = current.copy(searchResults = filtered)
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
                val alreadyAdded = current.members.any { it.id == event.user.id }
                if (!alreadyAdded) {
                    _uiState.value = current.copy(
                        members = current.members + event.user,
                        searchQuery = "",
                        searchResults = emptyList()
                    )
                    _searchQuery.value = ""
                }
            }

            is CreateEditTeamUiEvent.RemoveMember -> {
                if (event.user.id != current.ownerID) {
                    _uiState.value = current.copy(
                        members = current.members - event.user
                    )
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
        if (state.members.isEmpty()) {
            _uiState.value = state.copy(nameError = "Add at least one member")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true)
            delay(1000) 
            try {
                // Actualizamos el MockData global para que el cambio sea persistente en la sesión
                TeamsMockData.addTeam(state.teamName.trim(), state.members)

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
