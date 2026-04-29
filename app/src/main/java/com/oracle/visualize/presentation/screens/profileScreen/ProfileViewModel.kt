package com.oracle.visualize.presentation.screens.profileScreen

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
import com.oracle.visualize.presentation.screens.createChartScreen.CreateChartUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {



    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    var profileImage by mutableStateOf<Int>(R.drawable.profile_placeholder)
        private set

    var userName by mutableStateOf("Username Placeholder")
        private set

    var email by mutableStateOf("placeholder email")
        private set

    var selectedPalette by mutableStateOf(ChartPalette.THEME1)
        private set

    fun fetchUserData(): Result<Unit> {
        Log.d("ProfileViewModel", "PLACEHOLDER FETCH, REPLACE WITH REAL LOGIC LATER.")
        return Result.success(Unit)
    }


    fun onProfileImageChange(image: Int) {
        profileImage = image
    }

    fun onUserNameChange(name: String) {
        userName = name
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPaletteChange(palette: ChartPalette) {
        selectedPalette = palette
    }

    fun setUiState() {
        viewModelScope.launch {
            fetchUserData()
                .onSuccess {
                    _uiState.value = ProfileUiState.Ready(userName, email, profileImage, selectedPalette)
                }
                .onFailure {
                    _uiState.value = ProfileUiState.Idle
                }
        }
    }

    init {
        setUiState()
    }

}