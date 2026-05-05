package com.oracle.visualize.presentation.screens.session

import androidx.lifecycle.ViewModel
import com.oracle.visualize.domain.usecases.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    fun hasActiveSession(): Boolean {
        return getCurrentUserUseCase() != null
    }
}