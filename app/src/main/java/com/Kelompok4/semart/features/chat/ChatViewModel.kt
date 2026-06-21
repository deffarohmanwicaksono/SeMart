package com.Kelompok4.semart.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.model.Chat
import com.Kelompok4.semart.data.remote.ChatSessionResponse
import com.Kelompok4.semart.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ChatState {
    object Idle : ChatState()
    object Loading : ChatState()
    data class ChatListSuccess(val chats: List<Chat>) : ChatState()
    data class ChatDetailSuccess(val session: ChatSessionResponse) : ChatState()
    data class Error(val message: String) : ChatState()
}

class ChatViewModel(private val repository: ChatRepository = ChatRepository()) : ViewModel() {
    private val _state = MutableStateFlow<ChatState>(ChatState.Idle)
    val state: StateFlow<ChatState> = _state

    fun loadChatList() {
        _state.value = ChatState.Loading
        viewModelScope.launch {
            repository.getChatList().fold(
                onSuccess = { _state.value = ChatState.ChatListSuccess(it) },
                onFailure = { _state.value = ChatState.Error(it.message ?: "Failed to load chats") }
            )
        }
    }

    fun loadChatSession(chatId: Int) {
        _state.value = ChatState.Loading
        viewModelScope.launch {
            repository.getChatSession(chatId).fold(
                onSuccess = { _state.value = ChatState.ChatDetailSuccess(it) },
                onFailure = { _state.value = ChatState.Error(it.message ?: "Failed to load session") }
            )
        }
    }

    fun sendMessage(chatId: Int, message: String) {
        viewModelScope.launch {
            repository.sendMessage(chatId, message).fold(
                onSuccess = { loadChatSession(chatId) }, // Reload session on success
                onFailure = { _state.value = ChatState.Error(it.message ?: "Failed to send message") }
            )
        }
    }

    fun createChat(sellerId: Int, productId: Int, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            repository.createChat(sellerId, productId).fold(
                onSuccess = { onSuccess(it) },
                onFailure = { _state.value = ChatState.Error(it.message ?: "Failed to create chat") }
            )
        }
    }
}
