package com.oracle.visualize.presentation.screens.shareScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.repositories.TeamRepository
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * ViewModel for the Share and Post screen.
 * Manages team selections and user search suggestions for sharing visualizations.
 *
 * @property teamRepository Repository to fetch team information.
 * @property getUserSuggestionsUseCase Use case to search for users by email.
 */
@HiltViewModel
class ShareAndPostViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val getUserSuggestionsUseCase: GetUserSuggestionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Loading)
    val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    // TODO: This will be acquired from the Auth Repository eventually.
    val userID = "e9Nk8XrxHJAtwN3Hf2FL"

    init {
        loadData()
        setupSearchDebounce()
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .filter { it.isNotBlank() }
                .collect { query ->
                    getUserSuggestionsUseCase(query.lowercase().trim()).fold(
                        onSuccess = { results ->
                            updateSuggestions(results)
                        },
                        onFailure = { error ->
                            Log.e("ShareAndPostViewModel", "Error fetching user suggestions: ${error.message}")
                            updateSuggestions(emptyList())
                        }
                    )
                }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = ShareUiState.Loading
            try {
                val myTeams = teamRepository.getTeamsOwnedByUser(userID)
                val teamsImIn = teamRepository.getTeamsUserIsIn(userID)
                val suggestedUsers = emptyList<ShareUser>()

                _uiState.value = ShareUiState.Content(
                    myTeams        = myTeams,
                    teamsImIn      = teamsImIn,
                    suggestedUsers = suggestedUsers
                )
            } catch (e: Exception) {
                // Translates technical error
                val errorMessage = when (e) {
                    is AppError.NetworkError -> R.string.error_network
                    is AppError.ParsingError -> R.string.error_parsing
                    else -> R.string.error_unknown_retry
                }

                Log.e("ShareAndPostViewModel", "Failed to load initial data", e)
                _uiState.value = ShareUiState.Error(errorMessage)
            }
        }
    }

    private fun updateSuggestions(users: List<ShareUser>) {
        val current = _uiState.value as? ShareUiState.Content ?: return
        _uiState.value = current.copy(suggestedUsers = users)
    }

    fun onEvent(event: ShareUiEvent) {
        val current = _uiState.value as? ShareUiState.Content ?: return
        _uiState.value = when (event) {

            is ShareUiEvent.EmailQueryChanged -> {
                _searchQuery.value = event.query
                val newSuggestions = if (event.query.isEmpty()) emptyList() else current.suggestedUsers
                current.copy(emailQuery = event.query, suggestedUsers = newSuggestions)
            }

            is ShareUiEvent.AddUserByEmail -> {
                val userToAdd = current.suggestedUsers.find { it.email == event.email }
                    ?: ShareUser(
                        id = event.email,
                        username = event.email.substringBefore("@"),
                        email = event.email,
                        profilePictureURL = null
                    )

                val isAlreadySelected = current.selectedUsers.any { it.id == userToAdd.id }

                if (!isAlreadySelected) {
                    val updatedContent = current.copy(
                        selectedUsers = current.selectedUsers + userToAdd,
                        emailQuery = "",
                        suggestedUsers = emptyList()
                    )
                    updatedContent.copy(hasChanges = computeHasChanges(updatedContent))
                } else {
                    current.copy(
                        emailQuery = "",
                        suggestedUsers = emptyList()
                    )
                }
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
                // TODO: call UseCase to persist share action
                current
            }
        }
    }

    private fun computeHasChanges(state: ShareUiState.Content): Boolean =
        state.selectedUsers.isNotEmpty() || state.selectedTeamIds.isNotEmpty()
}
