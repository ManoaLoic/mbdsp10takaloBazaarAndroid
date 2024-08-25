package com.tpt.takalobazaar.screens.notifications


import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.tpt.takalobazaar.models.NotificationState
import com.tpt.takalobazaar.sealed.UiState

class NotificationViewModel : ViewModel() {
    private val _notificationState = mutableStateOf(NotificationState())
    val notificationState: State<NotificationState> = _notificationState
    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState> = _uiState

    fun showNotification(title: String, message: String) {
        _notificationState.value = NotificationState(title, message, true)
    }

    fun hideNotification() {
        _notificationState.value = NotificationState(isVisible = false)
    }
}
