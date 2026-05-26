package com.oracle.visualize.presentation.screens.profileScreen

import android.net.Uri
import com.oracle.visualize.ui.theme.ChartPalette


sealed interface ProfileUiState {
    object Idle : ProfileUiState

    data class Ready(
        val username: String,
        val eMail: String,
        val image: String,
        val chartTheme: String
    ) : ProfileUiState

    data class PfpUpload(
        val pfp: String? = null
    ) : ProfileUiState
}
