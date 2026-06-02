package com.oracle.visualize.presentation.screens.createEditTeamScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.team.CreateTeamUseCase
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import com.oracle.visualize.domain.usecases.team.GetUsersTeamsUseCase
import com.oracle.visualize.domain.usecases.team.UpdateTeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateEditTeamUiState>(CreateEditTeamUiState.Loading)
    val uiState: StateFlow<CreateEditTeamUiState> = _uiState.asStateFlow()

    private val _navigateBack = MutableStateFlow(false)
    val navigateBack: StateFlow<Boolean> = _navigateBack.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val teamIdArg: String? = savedStateHandle.get<String>("teamId")
    private val userID: String = authRepository.getCurrentUserID() ?: ""

    init {
        loadInitialData()
        setupSearchDebounce()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CreateEditTeamUiState.Loading

            if (teamIdArg != null) {
                when (val result = getUsersTeamsUseCase.getTeamsUserOwns(userID)) {
                    is AppResult.Success -> {
                        val team = result.data.find { it.id == teamIdArg }
                        _uiState.value = CreateEditTeamUiState.Content(
                            teamId      = teamIdArg,
                            teamName    = team?.name ?: "",
                            members     = team?.members ?: emptyList(),
                            ownerID     = userID,
                            suggestions = emptyList()
                        )
                    }
                    is AppResult.Error -> {
                        val errorResId = when (result.error) {
                            is AppError.NetworkError -> R.string.error_network
                            else -> R.string.error_team_load_failed
                        }
                        _uiState.value = CreateEditTeamUiState.Error(errorResId)
                    }
                }

            } else {
                val ownedDeferred  = async { getUsersTeamsUseCase.getTeamsUserOwns(userID) }
                val memberDeferred = async { getUsersTeamsUseCase.getTeamsUserIsIn(userID) }

                val ownerResult = ownedDeferred.await()
                val memberResult = memberDeferred.await()

                val ownedTeams  = if (ownerResult is AppResult.Success) ownerResult.data else emptyList()
                val memberTeams = if (memberResult is AppResult.Success) memberResult.data else emptyList()

                val ownerAsUser = (ownedTeams + memberTeams)
                    .flatMap { it.members }
                    .firstOrNull { it.id == userID }

                val suggestions = (ownedTeams + memberTeams)
                    .flatMap { it.members }
                    .filter    { it.id != userID }
                    .distinctBy { it.id }

                _uiState.value = CreateEditTeamUiState.Content(
                    ownerID     = userID,
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
                when (val result = getUserSuggestionsUseCase(query.lowercase().trim())) {
                    is AppResult.Success -> {
                        _uiState.value = current.copy(
                            searchResults = result.data.filter { r ->
                                current.members.none { it.id == r.id }
                            }
                        )
                    }
                    is AppResult.Error -> {}
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
                if (event.user.id != current.ownerID)
                    _uiState.value = current.copy(members = current.members - event.user)
            }

            is CreateEditTeamUiEvent.RequestBack -> {
                if (current.hasUnsavedChanges) {
                    _uiState.value = current.copy(showUnsavedChangesDialog = true)
                } else {
                    _navigateBack.value = true
                }
            }

            is CreateEditTeamUiEvent.ConfirmDiscard -> {
                _uiState.value = current.copy(showUnsavedChangesDialog = false)
                _navigateBack.value = true
            }

            is CreateEditTeamUiEvent.DismissUnsavedChangesDialog ->
                _uiState.value = current.copy(showUnsavedChangesDialog = false)

            is CreateEditTeamUiEvent.Submit -> submitTeam(current)
        }
    }

    private fun submitTeam(state: CreateEditTeamUiState.Content) {
        if (state.teamName.isBlank()) {
            _uiState.value = state.copy(nameError = R.string.error_team_name_empty)
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true)
            // ownerID must not be in membersIDs — exclude it explicitly
            val memberIDs = state.members.map { it.id }.filter { it != state.ownerID }
            val result = if (state.isEditMode)
                updateTeamUseCase(state.teamId!!, memberIDs, state.teamName.trim())
            else
                createTeamUseCase(memberIDs, state.teamName.trim(), state.ownerID)

            when (result) {
                is AppResult.Success -> {
                    _uiState.value = CreateEditTeamUiState.Success
                }
                is AppResult.Error -> {
                    _uiState.value = state.copy(
                        isSubmitting = false,
                        nameError    = R.string.error_team_saved_failed
                    )
                }
            }
        }
    }
}
