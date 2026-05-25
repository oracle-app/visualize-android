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
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val uid = auth.currentUser?.uid ?: ""



    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private suspend fun fetchUserData(): Result<Unit> {

        if (uid == null) {
            return Result.failure(AppError.AuthFailed())
        }

        return try {


            val user = userRepository.getUserByUserID(uid)
                ?: return Result.failure(AppError.NotFound("User not found for id: $uid"))

            // If the uid is successful, then take those values and bring them to the uiState

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
            userRepository.setChartTheme(uid, selectedPalette)
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
            val url = userRepository.uploadProfilePicture(uid, uri)
            userRepository.setProfilePicture(uid, url)
            fetchUserData()
        }
    }


    fun logout() {
        auth.signOut()
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
