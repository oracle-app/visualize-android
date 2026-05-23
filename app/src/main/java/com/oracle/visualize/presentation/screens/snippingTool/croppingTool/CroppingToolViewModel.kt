package com.oracle.visualize.presentation.screens.snippingTool.croppingTool

import android.graphics.Bitmap
import androidx.compose.ui.unit.IntRect
import androidx.lifecycle.ViewModel
import com.oracle.visualize.presentation.screens.snippingTool.lightweightSnippingTool.CroppingToolUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CroppingToolViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CroppingToolUiState())
    val uiState: StateFlow<CroppingToolUiState> = _uiState.asStateFlow()

    fun setCropRect(rect: IntRect) {
        _uiState.update { it.copy(cropRect = rect) }
    }

    fun confirmCrop(bitmap: Bitmap): Bitmap {
        val left = _uiState.value.cropRect.left.coerceIn(0, bitmap.width)
        val top = _uiState.value.cropRect.top.coerceIn(0, bitmap.height)
        val right = _uiState.value.cropRect.right.coerceIn(0, bitmap.width)
        val bottom = _uiState.value.cropRect.bottom.coerceIn(0, bitmap.height)
        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    }

    fun toggleConfirmDialog() {
        _uiState.update { it.copy(
            showConfirmDialog = !it.showConfirmDialog
        )
        }
    }

    fun toggleCancelDialog() {
        _uiState.update { it.copy(
            showCancelDialog = !it.showCancelDialog
        )
        }
    }

}
