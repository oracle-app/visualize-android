package com.oracle.visualize.presentation.screens.shareWithTeammatesScreen


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.GetUserSuggestionsUseCase
import com.oracle.visualize.presentation.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the "Share to More Teammates" screen.
 *
 * Loads the current sharing state of a visualization, allows searching for new
 * users by email, and supports removing previously-shared users.
 * The current user is excluded from suggestions to prevent the owner being
 * added as a sharedWith user (which causes a crash).
 *
 * @property getUserSuggestionsUseCase Use case for searching users by partial email.
 * @property authRepository Repository to obtain the current user ID.
 * @property savedStateHandle Provides the navigation arguments (visualizationId).
 */
@HiltViewModel
class ShareWithTeammatesViewModel @Inject constructor(
    private val getUserSuggestionsUseCase: GetUserSuggestionsUseCase,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val visualizationId: String =
        savedStateHandle.get<String>("visualizationId") ?: ""
    private val _uiState = MutableStateFlow<ShareWithTeammatesUiState>(ShareWithTeammatesUiState.Loading)
    val uiState: StateFlow<ShareWithTeammatesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    private val currentUserID: String = authRepository.getCurrentUserID()

    init {
        loadData()
        setupSearchDebounce()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = ShareWithTeammatesUiState.Loading
            try {
                // TODO: Replace with actual repository call to fetch shared users for visualizationId.
                val alreadySharedWith = emptyList<ShareUser>()

                _uiState.value = ShareWithTeammatesUiState.Content(
                    visualizationId = visualizationId,
                    sharedUsers     = alreadySharedWith
                )
            } catch (e: Exception) {
                _uiState.value = ShareWithTeammatesUiState.Error(
                    "Failed to load sharing info. Please try again."
                )
            }
        }
    }

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
                            // Exclude the current user (owner) and users already shared with.
                            // Owner being in sharedWith causes the app to crash.
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

    fun onEvent(event: ShareWithTeammatesUiEvent) {
        val current = _uiState.value as? ShareWithTeammatesUiState.Content ?: return
        when (event) {

            is ShareWithTeammatesUiEvent.EmailQueryChanged -> {
                _searchQuery.value = event.query
                val suggestions = if (event.query.isEmpty()) emptyList() else current.suggestedUsers
                _uiState.value = current.copy(emailQuery = event.query, suggestedUsers = suggestions)
            }

            is ShareWithTeammatesUiEvent.SelectSuggestion -> {
                // Guard: never add the current user as a shared user.
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

            is ShareWithTeammatesUiEvent.ConfirmShare -> {
                viewModelScope.launch {
                    updateContent { it.copy(isSubmitting = true) }
                    // TODO: call VisualizationRepository.updateSharedUsers(visualizationId, sharedUsers)
                    updateContent { it.copy(isSubmitting = false) }
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
