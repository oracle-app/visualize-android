package com.oracle.visualize.presentation.screens.snipPreviewScreen

data class SnipPreviewUIState(
    val caption: String = "",
    val showConfirmDialog: Boolean = false,
    val isSharing: Boolean = false,
    val errorMessage: Int? = null,
    val shareCompleted: Boolean = false
)
