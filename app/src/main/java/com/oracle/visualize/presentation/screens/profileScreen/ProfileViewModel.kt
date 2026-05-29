package com.oracle.visualize.presentation.screens.profileScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.usecases.ClearChartCacheUseCase
import com.oracle.visualize.domain.usecases.ClearFeedCacheUseCase
import com.oracle.visualize.domain.usecases.DeleteProfilePictureUseCase
import com.oracle.visualize.domain.usecases.GetCurrentUserUseCase
import com.oracle.visualize.domain.usecases.GetUserByIDUseCase
import com.oracle.visualize.domain.usecases.LogoutUseCase
import com.oracle.visualize.domain.usecases.SetChartThemeUseCase
import com.oracle.visualize.domain.usecases.UpdatePfpUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val setChartThemeUseCase: SetChartThemeUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updatePfpUseCase: UpdatePfpUseCase,
    private val getUserByIDUseCase: GetUserByIDUseCase,
    private val deleteProfilePictureUseCase: DeleteProfilePictureUseCase,
    private val clearChartCacheUseCase: ClearChartCacheUseCase,
    private val clearFeedCacheUseCase: ClearFeedCacheUseCase
) : ViewModel() {

    private val currentUser = getCurrentUserUseCase()
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private suspend fun fetchUserData(): Result<Unit> {
        val uid = currentUser?.uid
            ?: return Result.failure(AppError.AuthFailed())

        return getUserByIDUseCase(uid).fold(
            onSuccess = { user ->
                _uiState.value = ProfileUiState.Ready(user.username, user.email, user.profilePictureURL, user.chartTheme)
                Result.success(Unit)
            },
            onFailure = { e ->
                Log.e("ProfileViewModel", "Failed to fetch user: ${e.message}")
                Result.failure(e)
            }
        )
    }

    private var chartThemeJob: Job? = null

    fun setChartTheme(selectedPalette: String) {
        _uiState.update { current ->
            if (current is ProfileUiState.Ready) current.copy(chartTheme = selectedPalette)
            else current
        }
        chartThemeJob?.cancel()
        chartThemeJob = viewModelScope.launch {
            delay(500)
            setChartThemeUseCase(currentUser?.uid?: "", selectedPalette).onFailure { exception ->
                Log.e("ProfileViewModel", "Failed to set chart theme: ${exception.message}")
            }
        }
    }

    fun setUiState() {
        viewModelScope.launch {
            fetchUserData()
                .onFailure {
                    _uiState.value = ProfileUiState.Idle
                }
        }
    }

    fun setPfpUploadUi() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.PfpUpload()
        }
    }

    fun setPfpCapturedValue(uri: String) {
        _uiState.value = ProfileUiState.PfpUpload(pfp = uri)
    }



    fun updatePfp(uri: String) {
        viewModelScope.launch {
            updatePfpUseCase(currentUser?.uid ?: "", uri).fold(
                onSuccess = {
                    fetchUserData()
                },
                onFailure = { exception ->
                    Log.e("ProfileViewModel", "Failed to update profile picture: ${exception.message}")
                }
            )
        }
    }


    fun logout() {
        viewModelScope.launch {
            clearFeedCacheUseCase()
            clearChartCacheUseCase()
            logoutUseCase()
        }
    }

    fun deleteProfilePicture() {
        viewModelScope.launch {
            deleteProfilePictureUseCase(currentUser?.uid ?: "").onFailure { e ->
                Log.e("ProfileViewModel", "Failed to delete profile picture: ${e.message}")
            }
        }
    }

    init {
        setUiState()
    }

}
