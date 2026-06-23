package com.Kelompok4.semart.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.model.Chat
import com.Kelompok4.semart.data.model.Message
import com.Kelompok4.semart.data.remote.ChatSessionResponse
import com.Kelompok4.semart.data.remote.ReverbMessageData
import com.Kelompok4.semart.data.remote.ReverbWebSocketClient
import com.Kelompok4.semart.data.remote.WebSocketStatus
import com.Kelompok4.semart.data.remote.SessionManager
import com.Kelompok4.semart.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ChatState {
    object Idle    : ChatState()
    object Loading : ChatState()
    data class ChatListSuccess(val chats: List<Chat>) : ChatState()
    data class ChatDetailSuccess(val session: ChatSessionResponse) : ChatState()
    data class Error(val message: String) : ChatState()
}

class ChatViewModel(
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<ChatState>(ChatState.Idle)
    val state: StateFlow<ChatState> = _state

    // ── WebSocket ─────────────────────────────────────────────────────────────

    private val reverbClient = ReverbWebSocketClient()

    /** Status koneksi WebSocket yang bisa ditampilkan di UI */
    val wsStatus: StateFlow<WebSocketStatus> get() = _wsStatus
    private val _wsStatus = MutableStateFlow<WebSocketStatus>(WebSocketStatus.Disconnected)

    private var activeChatId: Int = -1

    /** Hubungkan WebSocket ke channel private-chat.{chatId} */
    fun connectWebSocket(chatId: Int) {
        activeChatId = chatId
        reverbClient.connect(chatId)

        // Pantau status koneksi
        viewModelScope.launch {
            reverbClient.status.collect { status ->
                _wsStatus.value = status
            }
        }

        // Pantau pesan masuk real-time
        viewModelScope.launch {
            reverbClient.incomingMessage.collect { incomingMsg ->
                appendIncomingMessage(incomingMsg)
            }
        }
    }

    /** Tutup WebSocket — panggil saat layar chat ditinggalkan */
    fun disconnectWebSocket() {
        reverbClient.disconnect()
        activeChatId = -1
    }

    // ── Append pesan baru ke state tanpa reload penuh ─────────────────────────

    private fun appendIncomingMessage(incoming: ReverbMessageData) {
        val currentState = _state.value
        if (currentState !is ChatState.ChatDetailSuccess) return

        val session = currentState.session

        // Cegah duplikat jika pesan sudah ada
        if (session.messages.any { it.id == incoming.id }) return

        val newMessage = Message(
            id         = incoming.id,
            senderId   = incoming.senderId,
            senderName = incoming.senderName,
            isLink     = incoming.isPurchaseLink,
            message    = incoming.message,
            createdAt  = incoming.createdAt
        )

        val updatedMessages = session.messages + newMessage
        val updatedSession  = session.copy(messages = updatedMessages)
        _state.value = ChatState.ChatDetailSuccess(updatedSession)
    }

    // ── REST API Calls ────────────────────────────────────────────────────────

    fun loadChatList() {
        _state.value = ChatState.Loading
        viewModelScope.launch {
            repository.getChatList(pov = "buyer").fold(
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
                onSuccess = {
                    // Pesan sendiri akan datang via REST response; pihak lain lewat WebSocket
                    // Tambahkan pesan sendiri langsung ke state agar terasa instan
                    appendSentMessage(it)
                },
                onFailure = { _state.value = ChatState.Error(it.message ?: "Failed to send message") }
            )
        }
    }

    /** Tambahkan pesan yang baru dikirim (milik sendiri) langsung ke UI */
    private fun appendSentMessage(sentMessage: Message) {
        val currentState = _state.value
        if (currentState !is ChatState.ChatDetailSuccess) return
        val session = currentState.session
        if (session.messages.any { it.id == sentMessage.id }) return
        val updatedSession = session.copy(messages = session.messages + sentMessage)
        _state.value = ChatState.ChatDetailSuccess(updatedSession)
    }

    fun createChat(sellerId: Int, productId: Int, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            repository.createChat(sellerId, productId).fold(
                onSuccess = { onSuccess(it) },
                onFailure = { _state.value = ChatState.Error(it.message ?: "Failed to create chat") }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        reverbClient.disconnect()
    }
}
