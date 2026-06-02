package com.oracle.visualize.presentation.screens.notificationScreen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oracle.visualize.R
import com.oracle.visualize.domain.exceptions.AppError
import com.oracle.visualize.domain.exceptions.AppResult
import com.oracle.visualize.domain.repositories.AuthRepository
import com.oracle.visualize.domain.usecases.notification.GetNotificationsForUserUseCase
import com.oracle.visualize.domain.usecases.notification.GroupNotificationsUseCase
import com.oracle.visualize.domain.usecases.notification.MarkAllNotificationsAsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Notification screen.
 * Handles fetching notifications based on hte current user.
 * After a user visits the screen once, all visualizations currently loaded
 * will be marked as read.
 *
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsForUserUseCase: GetNotificationsForUserUseCase,
    private val groupNotificationsUseCase: GroupNotificationsUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUIState())
    val uiState: StateFlow<NotificationUIState> = _uiState.asStateFlow()

    private var currentUserID: String = ""

    init {
        currentUserID = authRepository.getCurrentUserID().toString()
        if (currentUserID.isBlank()) {
            _uiState.update { it.copy(error = R.string.error_unknown_retry) }
        } else {
            loadNotifications()
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = getNotificationsForUserUseCase(currentUserID)) {
                is AppResult.Success -> {
                    val grouped = groupNotificationsUseCase(result.data)
                    _uiState.update { it.copy(groupedNotifications = grouped, isLoading = false) }
                }

                is AppResult.Error -> {
                    val uiErrorMessage = when (result.error) {
                        is AppError.NetworkError -> R.string.error_network
                        is AppError.NotFound -> R.string.error_com_not_found
                        else -> R.string.error_unknown_retry
                    }
                    _uiState.update { it.copy(error = uiErrorMessage, isLoading = false) }
                }
            }
        }
    }
}
