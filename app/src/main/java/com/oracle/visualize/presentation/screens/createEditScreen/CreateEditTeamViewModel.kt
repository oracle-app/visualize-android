package com.oracle.visualize.presentation.screens.createEditScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.models.enums.UserType
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.UserRepository
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
    private val userRepository: UserRepository, // INYECTAMOS USER REPOSITORY
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateEditTeamUiState>(CreateEditTeamUiState.Loading)
    val uiState: StateFlow<CreateEditTeamUiState> = _uiState.asStateFlow()

    private val _navigateBack = MutableStateFlow(false)
    val navigateBack: StateFlow<Boolean> = _navigateBack.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val teamIdArg: String? = savedStateHandle.get<String>("teamId")

    init {
        loadInitialData()
        setupSearchDebounce()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CreateEditTeamUiState.Loading

            try {
                val currentUserID = authRepository.getCurrentUserID()
                val currentUser = userRepository.getUserByUserID(currentUserID)

                if (teamIdArg != null) {
                    val ownedDeferred  = async { getUsersTeamsUseCase.getTeamsUserOwns(currentUserID) }
                    val memberDeferred = async { getUsersTeamsUseCase.getTeamsUserIsIn(currentUserID) }

                    val ownedTeams  = ownedDeferred.await().getOrNull()  ?: emptyList()
                    val memberTeams = memberDeferred.await().getOrNull() ?: emptyList()

                    val teamToEdit = (ownedTeams + memberTeams).find { it.id == teamIdArg }

                    if (teamToEdit != null) {
                        val isOwner = teamToEdit.ownerID == currentUserID
                        val isAdmin = currentUser.userType == UserType.ADMIN

                        if (isOwner || isAdmin) {
                            _uiState.value = CreateEditTeamUiState.Content(
                                teamId      = teamIdArg,
                                teamName    = teamToEdit.name,
                                members     = teamToEdit.members,
                                ownerID     = teamToEdit.ownerID,
                                suggestions = emptyList()
                            )
                        } else {
                            _uiState.value = CreateEditTeamUiState.Error("No tienes permiso para editar este equipo.")
                        }
                    } else {
                        _uiState.value = CreateEditTeamUiState.Error("No se encontró el equipo.")
                    }
                } else {
                    val ownedDeferred  = async { getUsersTeamsUseCase.getTeamsUserOwns(currentUserID) }
                    val memberDeferred = async { getUsersTeamsUseCase.getTeamsUserIsIn(currentUserID) }

                    val ownedTeams  = ownedDeferred.await().getOrNull()  ?: emptyList()
                    val memberTeams = memberDeferred.await().getOrNull() ?: emptyList()

                    val ownerAsUser = (ownedTeams + memberTeams)
                        .flatMap { it.members }
                        .firstOrNull { it.id == currentUserID }

                    val suggestions = (ownedTeams + memberTeams)
                        .flatMap { it.members }
                        .filter    { it.id != currentUserID }
                        .distinctBy { it.id }

                    _uiState.value = CreateEditTeamUiState.Content(
                        ownerID     = currentUserID,
                        members     = listOfNotNull(ownerAsUser),
                        suggestions = suggestions
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CreateEditTeamUiState.Error(e.message ?: "Error al cargar la información")
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
            _uiState.value = state.copy(nameError = "Team name cannot be empty")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSubmitting = true)
            val memberIDs = state.members.map { it.id }.filter { it != state.ownerID }
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
