package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.TeamRepository
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import com.oracle.visualize.domain.usecases.UpdateSharedUsersUseCase
import com.oracle.visualize.presentation.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareWithTeammatesViewModel @Inject constructor(
    private val getUserSuggestionsUseCase: GetUserSuggestionsUseCase,
    private val updateSharedUsersUseCase: UpdateSharedUsersUseCase,
    private val authRepository: AuthRepository,
    private val teamRepository: TeamRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShareWithTeammatesUiState>(ShareWithTeammatesUiState.Loading)
    val uiState: StateFlow<ShareWithTeammatesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    private val currentUserID: String = authRepository.getCurrentUserID()

    private val visualizationId: String =
        savedStateHandle.toRoute<NavRoutes.ShareWithTeammates>().visualizationId

    init {
        loadData()
        setupSearchDebounce()
    }

    // ─── Data loading ──────────────────────────────────────────────────────────

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = ShareWithTeammatesUiState.Loading
            try {
                val myTeamsDeferred   = async { teamRepository.getTeamsOwnedByUser(currentUserID) }
                val teamsImInDeferred = async { teamRepository.getTeamsUserIsIn(currentUserID) }

                val myTeams   = myTeamsDeferred.await()
                val teamsImIn = teamsImInDeferred.await()
                    .filter { team -> myTeams.none { it.id == team.id } }

                _uiState.value = ShareWithTeammatesUiState.Content(
                    visualizationId = visualizationId,
                    sharedUsers     = emptyList(),
                    myTeams         = myTeams,
                    teamsImIn       = teamsImIn
                )
            } catch (e: Exception) {
                _uiState.value = ShareWithTeammatesUiState.Error(
                    "Failed to load sharing info. Please try again."
                )
            }
        }
    }

    // ─── Search debounce ───────────────────────────────────────────────────────

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .filter { it.isNotBlank() }
                .collect { query ->
                    getUserSuggestionsUseCase(query.lowercase().trim()).fold(
                        onSuccess = { results ->
                            val current = _uiState.value as? ShareWithTeammatesUiState.Content
                                ?: return@fold
                            val filtered = results.filter { suggestion ->
                                suggestion.id != currentUserID &&
                                    current.sharedUsers.none { it.id == suggestion.id }
                            }
                            _uiState.value = current.copy(suggestedUsers = filtered)
                        },
                        onFailure = {
                            updateContent { it.copy(suggestedUsers = emptyList()) }
                        }
                    )
                }
        }
    }

    // ─── Events ────────────────────────────────────────────────────────────────

    fun onEvent(event: ShareWithTeammatesUiEvent) {
        val current = _uiState.value as? ShareWithTeammatesUiState.Content ?: return
        when (event) {

            is ShareWithTeammatesUiEvent.EmailQueryChanged -> {
                _searchQuery.value = event.query
                val suggestions = if (event.query.isEmpty()) emptyList() else current.suggestedUsers
                _uiState.value = current.copy(emailQuery = event.query, suggestedUsers = suggestions)
            }

            is ShareWithTeammatesUiEvent.SelectSuggestion -> {
                val alreadyShared = current.sharedUsers.any { it.id == event.user.id }
                val isOwner       = event.user.id == currentUserID
                if (!alreadyShared && !isOwner) {
                    _uiState.value = current.copy(
                        sharedUsers    = current.sharedUsers + event.user,
                        emailQuery     = "",
                        suggestedUsers = emptyList()
                    )
                    _searchQuery.value = ""
                }
            }

            is ShareWithTeammatesUiEvent.RequestRemoveUser -> {
                _uiState.value = current.copy(removeDialogForUser = event.user)
            }

            is ShareWithTeammatesUiEvent.DismissRemoveDialog -> {
                _uiState.value = current.copy(removeDialogForUser = null)
            }

            is ShareWithTeammatesUiEvent.ConfirmRemoveUser -> {
                _uiState.value = current.copy(
                    sharedUsers         = current.sharedUsers.filter { it.id != event.user.id },
                    removeDialogForUser = null
                )
            }

            is ShareWithTeammatesUiEvent.ToggleTeam -> {
                val newSelected = if (event.teamId in current.selectedTeamIds) {
                    current.selectedTeamIds - event.teamId
                } else {
                    current.selectedTeamIds + event.teamId
                }
                _uiState.value = current.copy(selectedTeamIds = newSelected)
            }

            is ShareWithTeammatesUiEvent.ConfirmShare -> {
                val userIds = current.sharedUsers.map { it.id }
                val teamIds = current.selectedTeamIds.toList()

                _uiState.value = current.copy(isSubmitting = true, errorMessage = null)

                viewModelScope.launch {
                    updateSharedUsersUseCase(visualizationId, userIds, teamIds).fold(
                        onSuccess = {
                            updateContent { it.copy(isSubmitting = false, shareSuccess = true) }
                        },
                        onFailure = { error ->
                            updateContent {
                                it.copy(
                                    isSubmitting = false,
                                    errorMessage = error.message ?: "Failed to share. Please try again."
                                )
                            }
                        }
                    )
                }
            }

            is ShareWithTeammatesUiEvent.BackPressed -> { /* handled in View */ }
        }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private fun updateContent(
        block: (ShareWithTeammatesUiState.Content) -> ShareWithTeammatesUiState.Content
    ) {
        val current = _uiState.value as? ShareWithTeammatesUiState.Content ?: return
        _uiState.value = block(current)
    }
}
