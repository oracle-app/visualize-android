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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {



    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    var selectedPalette by mutableStateOf(ChartPalette.THEME1)
        private set

    suspend fun fetchUserData(): Result<Unit> {

        //This is a PLACEHOLDER uid, replace in implementation with a Firebase get current user.

        val uid = "4HgGnKxDgthKf6GuQZqF1NGqP133"

        if (uid == null) {
            return Result.failure(AppError.AuthFailed())
        }

        return try {


            val user = userRepository.getUserByUserID(uid)
                ?: return Result.failure(AppError.NotFound("User not found for id: $uid"))

            // This is a DEBUGGING log, remove when finished.

            Log.d("ProfileViewModel", "Profile picture URL: ${user.profilePictureURL}")

            // If the uid is successful, then take those values and bring them to the uiState

            _user.value = user
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

    fun onPaletteChange(palette: ChartPalette) {
        selectedPalette = palette
    }

    fun setUiState() {
        viewModelScope.launch {
            fetchUserData()
                .onFailure {
                    _uiState.value = ProfileUiState.Idle
                }
        }
    }

    fun setPfpUpload() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.PfpUpload()
        }
    }

    fun onPfpCaptured(uri: Uri) {
        _uiState.value = ProfileUiState.PfpUpload(pfp = uri)
    }

    init {
        setUiState()
    }

}
