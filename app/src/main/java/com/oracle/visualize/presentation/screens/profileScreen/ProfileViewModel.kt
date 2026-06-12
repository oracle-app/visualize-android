package com.oracle.visualize.presentation.screens.profileScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.usecases.cache.ClearChartCacheUseCase
import com.oracle.visualize.domain.usecases.cache.ClearFeedCacheUseCase
import com.oracle.visualize.domain.usecases.profile.DeleteProfilePictureUseCase
import com.oracle.visualize.domain.usecases.auth.GetCurrentUserUseCase
import com.oracle.visualize.domain.usecases.auth.GetUserByIDUseCase
import com.oracle.visualize.domain.usecases.auth.LogoutUseCase
import com.oracle.visualize.domain.usecases.chart.SetChartThemeUseCase
import com.oracle.visualize.domain.usecases.profile.UpdatePfpUseCase
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

    private suspend fun fetchUserData() {
        val uid = currentUser?.uid
        if (uid == null) {
            _uiState.value = ProfileUiState.Idle
            return
        }

        when (val result = getUserByIDUseCase(uid)) {
            is AppResult.Success -> {
                val user = result.data
                _uiState.value = ProfileUiState.Ready(
                    username = user.username,
                    eMail = user.email,
                    image = user.profilePictureURL,
                    chartTheme = user.chartTheme
                )
            }

            is AppResult.Error -> {
                Log.e("ProfileViewModel", "Failed to fetch user: ${result.error.message}")
                _uiState.value = ProfileUiState.Idle
            }
        }
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
            when (val result = setChartThemeUseCase(currentUser?.uid?: "", selectedPalette)) {
                is AppResult.Success -> {}
                is AppResult.Error -> {
                    Log.e("ProfileViewModel", "Failed to set chart theme: ${result.error.message}")
                }
            }
        }
    }

    fun setUiState() {
        viewModelScope.launch {
            fetchUserData()
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
            when (val result = updatePfpUseCase(currentUser?.uid ?: "", uri)) {
                is AppResult.Success -> {
                    fetchUserData()
                }
                is AppResult.Error -> {
                    Log.e("ProfileViewModel", "Failed to update profile picture: ${result.error.message}")
                }
            }
        }
    }


    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            clearFeedCacheUseCase()
            clearChartCacheUseCase()
            onLogoutComplete()
        }
    }

    fun deleteProfilePicture() {
        viewModelScope.launch {
            when (val result = deleteProfilePictureUseCase(currentUser?.uid ?: "")) {
                is AppResult.Success -> {}
                is AppResult.Error -> {
                    Log.e("ProfileViewModel", "Failed to delete profile picture: ${result.error.message}")
                }
            }
        }
    }

    init {
        setUiState()
    }

}
