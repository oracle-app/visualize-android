package com.oracle.visualize.presentation.screens.profileScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.oracle.visualize.R
import com.oracle.visualize.ui.theme.ChartPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    var profileImage by mutableStateOf<Int>(R.drawable.profile_placeholder)
        private set

    var userName by mutableStateOf("Diana Escalante")
        private set

    var email by mutableStateOf("dianaescalante@gmail.com")
        private set

    var selectedPalette by mutableStateOf(ChartPalette.THEME1)
        private set


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
}