package com.oracle.visualize.presentation.screens.profileScreen

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.oracle.visualize.R
import com.oracle.visualize.ui.theme.ChartPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.models.User
import com.oracle.visualize.domain.repositories.UserRepository
import com.oracle.visualize.domain.usecases.GetCurrentUserUseCase
import com.oracle.visualize.domain.usecases.LoginUseCase
import com.oracle.visualize.domain.usecases.LogoutUseCase
import com.oracle.visualize.domain.usecases.SetChartThemeUseCase
import com.oracle.visualize.domain.usecases.UpdatePfpUseCase
import com.oracle.visualize.presentation.screens.createChartScreen.CreateChartUiState
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
    private val updatePfpUseCase: UpdatePfpUseCase
) : ViewModel() {

    private val currentUser = getCurrentUserUseCase()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private suspend fun fetchUserData(): Result<Unit> {
        val uid = currentUser?.uid
            ?: return Result.failure(AppError.AuthFailed())

        return try {
            val user = userRepository.getUserByUserID(uid)
                ?: return Result.failure(AppError.NotFound("User not found for id: $uid"))

            _uiState.value = ProfileUiState.Ready(user.username, user.email, user.profilePictureURL, user.chartTheme)
            Result.success(Unit)

        } catch (e: AppError.NotFound) {
            Log.e("ProfileViewModel", "User not found: ${e.message}")
            Result.failure(e)
        } catch (e: AppError.NetworkError) {
            Log.e("ProfileViewModel", "Network error: ${e.message}")
            Result.failure(e)
        } catch (e: AppError.ParsingError) {
            Log.e("ProfileViewModel", "Failed to parse user data: ${e.message}")
            Result.failure(e)
        } catch (e: AppError.AuthFailed) {
            Log.e("ProfileViewModel", "Auth error: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Unexpected error: ${e.message}")
            Result.failure(e)
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
            setChartThemeUseCase(selectedPalette).onFailure { exception ->
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

    fun setPfpCapturedValue(uri: Uri) {
        _uiState.value = ProfileUiState.PfpUpload(pfp = uri)
    }



    fun updatePfp(uri: Uri) {
        viewModelScope.launch {
            updatePfpUseCase(uri.toString()).fold(
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
            logoutUseCase()
        }
    }

    fun deleteProfilePicture() {
        viewModelScope.launch {
            try {
                userRepository.deleteProfilePicture(uid)
            } catch (e: AppError.NotFound) {
                Log.e("ProfileViewModel", "Unexpected error: ${e.message}")
            }
            userRepository.setProfilePicture(uid, "")
        }
    }

    init {
        setUiState()
    }

}
