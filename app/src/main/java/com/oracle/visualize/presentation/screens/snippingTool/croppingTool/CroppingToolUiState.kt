package com.oracle.visualize.presentation.screens.snippingTool.lightweightSnippingTool

import androidx.compose.ui.unit.IntRect

data class CroppingToolUiState(
    val cropRect: IntRect = IntRect(0, 0, 0, 0),
    val showConfirmDialog: Boolean = false,
    val showCancelDialog: Boolean = false
)
