package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.oracle.visualize.R
import com.oracle.visualize.data.datasources.local.FeedCacheManager
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.repositories.TeamRepository
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.domain.repositories.VisualizationRepository
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import com.oracle.visualize.domain.usecases.visualization.UpdateSharedUsersUseCase
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
    private val visualizationRepository: VisualizationRepository,
    private val userRepository: UserRepository,
    private val feedCacheManager: FeedCacheManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShareWithTeammatesUiState>(ShareWithTeammatesUiState.Loading)
    val uiState: StateFlow<ShareWithTeammatesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    private val currentUserID: String = authRepository.getCurrentUserID() ?: ""

    private val visualizationId: String =
        savedStateHandle.toRoute<NavRoutes.ShareWithTeammates>().visualizationId

    init {
        loadData()
        setupSearchDebounce()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = ShareWithTeammatesUiState.Loading
            val myTeamsDeferred    = async { teamRepository.getTeamsOwnedByUser(currentUserID) }
            val teamsImInDeferred  = async { teamRepository.getTeamsUserIsIn(currentUserID) }
            val currentVizDeferred = async { visualizationRepository.getVisualizationById(visualizationId) }

            val myTeamsResult = myTeamsDeferred.await()
            val teamsImInResult = teamsImInDeferred.await()
            val currentVizResult = currentVizDeferred.await()

            if (
                myTeamsResult is AppResult.Success &&
                teamsImInResult is AppResult.Success &&
                currentVizResult is AppResult.Success
                ) {
                val myTeams = myTeamsResult.data
                val teamsImInRaw = teamsImInResult.data
                val currentViz = currentVizResult.data

                val teamsImIn = teamsImInRaw.filter { team -> myTeams.none() { it.id == team.id} }

                val existingUserIds = currentViz?.sharedWithUsers ?: emptyList()
                val existingTeamIds = (currentViz?.sharedWithTeams ?: emptyList()).toSet()

                val existingUsersResult = if (existingUserIds.isNotEmpty()) {
                    userRepository.getUsersByIDs(existingUserIds)
                } else {
                    AppResult.Success(emptyList())
                }

                if (existingUsersResult is AppResult.Success) {
                    _uiState.value = ShareWithTeammatesUiState.Content(
                        visualizationId = visualizationId,
                        sharedUsers = existingUsersResult.data ?: emptyList(),
                        selectedTeamIds = existingTeamIds,
                        myTeams = myTeams,
                        teamsImIn = teamsImIn
                    )
                } else {
                    _uiState.value = ShareWithTeammatesUiState.Error(R.string.error_share_load_failed)
                }
            }
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .filter { it.isNotBlank() }
                .collect { query ->
                    when (val result = getUserSuggestionsUseCase(query.lowercase().trim())){
                        is AppResult.Success -> {
                            val results = result.data
                            val current = _uiState.value as? ShareWithTeammatesUiState.Content ?: return@collect
                            val filtered = results.filter { suggestion ->
                                suggestion.id != currentUserID &&
                                    current.sharedUsers.none { it.id == suggestion.id }
                            }
                            _uiState.value = current.copy(suggestedUsers = filtered)
                        }
                        is AppResult.Error -> {
                            updateContent { it.copy(suggestedUsers = emptyList()) }
                        }
                    }
                }
        }
    }

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

            is ShareWithTeammatesUiEvent.RequestRemoveUser ->
                _uiState.value = current.copy(removeDialogForUser = event.user)

            is ShareWithTeammatesUiEvent.DismissRemoveDialog ->
                _uiState.value = current.copy(removeDialogForUser = null)

            is ShareWithTeammatesUiEvent.ConfirmRemoveUser ->
                _uiState.value = current.copy(
                    sharedUsers         = current.sharedUsers.filter { it.id != event.user.id },
                    removeDialogForUser = null
                )

            is ShareWithTeammatesUiEvent.ToggleTeam -> {
                val newSelected = if (event.teamId in current.selectedTeamIds)
                    current.selectedTeamIds - event.teamId
                else
                    current.selectedTeamIds + event.teamId
                _uiState.value = current.copy(selectedTeamIds = newSelected)
            }

            is ShareWithTeammatesUiEvent.ConfirmShare -> {
                val userIds = current.sharedUsers.map { it.id }
                val teamIds = current.selectedTeamIds.toList()

                _uiState.value = current.copy(isSubmitting = true, errorMessage = null)

                viewModelScope.launch {
                    when (val result = updateSharedUsersUseCase(visualizationId, userIds, teamIds)) {
                        is AppResult.Success -> {
                            feedCacheManager.clearCache()
                            updateContent { it.copy(isSubmitting = false, shareSuccess = true) }
                        }
                        is AppResult.Error -> {
                            updateContent {
                                it.copy(
                                    isSubmitting = false,
                                    errorMessage = R.string.error_share_failed
                                )
                            }
                        }
                    }
                }
            }

            is ShareWithTeammatesUiEvent.BackPressed -> { /* handled in View */ }
        }
    }

    private fun updateContent(
        block: (ShareWithTeammatesUiState.Content) -> ShareWithTeammatesUiState.Content
    ) {
        val current = _uiState.value as? ShareWithTeammatesUiState.Content ?: return
        _uiState.value = block(current)
    }
}
