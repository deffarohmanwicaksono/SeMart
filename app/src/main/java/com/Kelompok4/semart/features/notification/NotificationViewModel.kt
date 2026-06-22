package com.Kelompok4.semart.features.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.model.Notification
import com.Kelompok4.semart.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NotificationState {
    object Idle : NotificationState()
    object Loading : NotificationState()
    data class Success(val notifications: List<Notification>) : NotificationState()
    data class Error(val message: String) : NotificationState()
}

class NotificationViewModel(private val repository: NotificationRepository = NotificationRepository()) : ViewModel() {
    private val _state = MutableStateFlow<NotificationState>(NotificationState.Idle)
    val state: StateFlow<NotificationState> = _state

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    fun loadNotifications() {
        _state.value = NotificationState.Loading
        viewModelScope.launch {
            repository.getNotifications().fold(
                onSuccess = {
                    _state.value = NotificationState.Success(it)
                    // Backend marks all as read when fetching, so reset badge
                    _unreadCount.value = 0
                },
                onFailure = { _state.value = NotificationState.Error(it.message ?: "Gagal memuat notifikasi") }
            )
        }
    }

    fun fetchUnreadCount() {
        viewModelScope.launch {
            repository.getUnreadCount().fold(
                onSuccess = { _unreadCount.value = it },
                onFailure = { _unreadCount.value = 0 }
            )
        }
    }
}
