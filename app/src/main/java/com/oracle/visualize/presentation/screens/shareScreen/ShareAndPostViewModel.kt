package com.oracle.visualize.presentation.screens.shareScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.ShareUser
import com.oracle.visualize.domain.repositories.AuthRepository
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.oracle.visualize.domain.repositories.AnalyzeRepository
import com.oracle.visualize.domain.usecases.PublishVisualizationsInBulkUseCase
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.data.datasources.dtos.ChartResponseDTO

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
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
    private val repository: AnalyzeRepository,
    private val publishVisualizationsInBulkUseCase: PublishVisualizationsInBulkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Loading)
    val uiState: StateFlow<ShareUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private var userID = ""

    init {
        try {
            userID = authRepository.getCurrentUserID()
            loadData()
            setupSearchDebounce()
        } catch (e: Exception) {
            _uiState.value = ShareUiState.Error(R.string.error_unknown_retry)
        }
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
                confirmShare(current)
                current
            }
        }
    }

    private fun confirmShare(current: ShareUiState.Content) {
        viewModelScope.launch {
            _uiState.value = ShareUiState.Loading
            try {
                val shareArgs = savedStateHandle.toRoute<com.oracle.visualize.presentation.navigation.NavRoutes.ShareAndPost>()
                val taskId = shareArgs.taskId
                val selectedChartIndices = shareArgs.selectedChartIndices
                val customTitles = shareArgs.customTitles

                val sharedWithUsers = current.selectedUsers.map { it.id }
                val sharedWithTeams = current.selectedTeamIds.toList()

                // Step 1: Fetch Page 1 for all charts concurrently
                val firstPageDeferreds = selectedChartIndices.map { chartIndex ->
                    async {
                        repository.getPagedResultsDto(taskId, chartIndex, 1) to chartIndex
                    }
                }
                val firstPageResults = firstPageDeferreds.awaitAll()

                val chartPageJobs = mutableListOf<Deferred<Pair<Int, AppResult<ChartResponseDTO>>>>()
                val firstPagesMap = mutableMapOf<Int, ChartResponseDTO>()

                for ((res, chartIndex) in firstPageResults) {
                    if (res !is AppResult.Success) {
                        throw Exception((res as? AppResult.Error)?.error?.message ?: "Failed to fetch page 1 for chart $chartIndex")
                    }
                    val firstPageDto = res.data
                    firstPagesMap[chartIndex] = firstPageDto

                    // Step 2: Fetch remaining pages (2..totalPages) concurrently
                    val totalPages = firstPageDto.totalPages
                    for (page in 2..totalPages) {
                        chartPageJobs.add(
                            async {
                                chartIndex to repository.getPagedResultsDto(taskId, chartIndex, page)
                            }
                        )
                    }
                }

                // Await all additional pages in parallel
                val additionalPagesResults = chartPageJobs.awaitAll()

                // Group pages by chartIndex
                val pagesByChart = mutableMapOf<Int, MutableList<ChartResponseDTO>>()
                firstPagesMap.forEach { (chartIndex, firstPageDto) ->
                    pagesByChart.getOrPut(chartIndex) { mutableListOf() }.add(firstPageDto)
                }

                for ((chartIndex, res) in additionalPagesResults) {
                    if (res is AppResult.Success) {
                        pagesByChart.getOrPut(chartIndex) { mutableListOf() }.add(res.data)
                    } else {
                        throw Exception((res as? AppResult.Error)?.error?.message ?: "Failed to fetch page for chart $chartIndex")
                    }
                }

                // Step 3: Run CPU-intensive merging and JSON serialization on Dispatchers.Default
                val visualizations = withContext(Dispatchers.Default) {
                    selectedChartIndices.mapIndexed { i, chartIndex ->
                        val customTitle = customTitles.getOrElse(i) { "Visualization ${chartIndex + 1}" }
                        val pagesList = pagesByChart[chartIndex] ?: throw Exception("Missing pages for chart $chartIndex")
                        
                        // Sort pages to ensure they are merged in correct chronological order
                        pagesList.sortBy { it.page }

                        val firstPageDto = pagesList.first()
                        val mergedData = com.oracle.visualize.data.mapper.ChartMapper.mergePagedData(firstPageDto.chartType, pagesList)

                        val previewDto = firstPageDto.copy(chartName = customTitle)
                        val combinedDto = firstPageDto.copy(
                            chartName = customTitle,
                            page = 0,
                            preview = false,
                            totalPages = 1,
                            data = mergedData
                        )

                        com.oracle.visualize.domain.models.Visualization(
                            id = "",
                            authorID = userID,
                            title = customTitle,
                            configJSON = com.google.gson.Gson().toJson(combinedDto),
                            previewJSON = com.google.gson.Gson().toJson(previewDto),
                            sharedWithUsers = sharedWithUsers,
                            sharedWithTeams = sharedWithTeams,
                            createdAt = java.util.Date()
                        )
                    }
                }

                // Step 4: Publish visualizations in bulk to Firebase
                publishVisualizationsInBulkUseCase(visualizations).fold(
                    onSuccess = {
                        _uiState.value = ShareUiState.Success
                    },
                    onFailure = {
                        throw it
                    }
                )

            } catch (e: Exception) {
                Log.e("ShareAndPostViewModel", "Failed to share visualizations", e)
                _uiState.value = ShareUiState.Error(R.string.error_network)
            }
        }
    }

    private fun computeHasChanges(state: ShareUiState.Content): Boolean =
        state.selectedUsers.isNotEmpty() || state.selectedTeamIds.isNotEmpty()
}
