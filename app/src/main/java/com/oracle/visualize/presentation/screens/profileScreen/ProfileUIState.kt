package com.oracle.visualize.presentation.screens.profileScreen

import com.oracle.visualize.ui.theme.ChartPalette


sealed interface ProfileUiState {
    object Idle : ProfileUiState

    data class Ready(
        val Username: String,
        val eMail: String,
        val image: Int,
        val chartTheme: ChartPalette
    ) : ProfileUiState
}
