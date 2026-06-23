package com.Kelompok4.semart.data.remote

import android.util.Log
import com.Kelompok4.semart.BuildConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

// ── Model data untuk event MessageSent dari Reverb ───────────────────────────

data class ReverbMessageData(
    @SerializedName("id")          val id: Int,
    @SerializedName("chat_id")     val chatId: Int? = null,
    @SerializedName("sender_id")   val senderId: Int,
    @SerializedName("sender_name") val senderName: String? = null,
    @SerializedName("message")     val message: String? = null,
    @SerializedName("is_purchase_link") val isPurchaseLink: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null,
)

data class ReverbEvent(
    @SerializedName("event")   val event: String,
    @SerializedName("data")    val data: String? = null,
    @SerializedName("channel") val channel: String? = null,
)

// ── Status koneksi WebSocket ──────────────────────────────────────────────────

sealed class WebSocketStatus {
    object Connecting : WebSocketStatus()
    object Connected  : WebSocketStatus()
    object Subscribed : WebSocketStatus()
    data class Error(val message: String) : WebSocketStatus()
    object Disconnected : WebSocketStatus()
}

// ── Reverb WebSocket Client ───────────────────────────────────────────────────

class ReverbWebSocketClient {

    private val TAG = "ReverbWS"
    private val gson = Gson()

    private var webSocket: WebSocket? = null
    private var currentChatId: Int = -1
    private var socketId: String? = null

    // Flow yang bisa di-collect dari ViewModel / Composable
    private val _incomingMessage = MutableSharedFlow<ReverbMessageData>(extraBufferCapacity = 10)
    val incomingMessage: SharedFlow<ReverbMessageData> = _incomingMessage

    private val _status = MutableSharedFlow<WebSocketStatus>(extraBufferCapacity = 5)
    val status: SharedFlow<WebSocketStatus> = _status

    // OkHttpClient khusus WebSocket (timeout lebih longgar)
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)   // tetap open
        .connectTimeout(10, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)       // keep-alive
        .build()

    /**
     * Sambungkan ke Reverb dan subscribe ke channel private-chat.{chatId}.
     * Panggil dari ViewModel saat layar ChatDetail dibuka.
     */
    fun connect(chatId: Int) {
        if (webSocket != null && currentChatId == chatId) {
            Log.d(TAG, "Sudah terhubung ke chat $chatId, skip.")
            return
        }
        disconnect() // tutup koneksi lama jika ada

        currentChatId = chatId
        _status.tryEmit(WebSocketStatus.Connecting)

        val reverbHost   = BuildConfig.REVERB_HOST
        val reverbPort   = BuildConfig.REVERB_PORT
        val reverbAppKey = BuildConfig.REVERB_APP_KEY

        val wsUrl = "ws://$reverbHost:$reverbPort/app/$reverbAppKey" +
                "?protocol=7&client=android&version=8.0.0&flash=false"

        Log.d(TAG, "Connecting to: $wsUrl")

        val request = Request.Builder().url(wsUrl).build()
        webSocket = client.newWebSocket(request, ReverbListener(chatId))
    }

    /** Tutup koneksi WebSocket. Panggil saat layar ditinggalkan. */
    fun disconnect() {
        webSocket?.close(1000, "Screen closed")
        webSocket = null
        socketId = null
        currentChatId = -1
        _status.tryEmit(WebSocketStatus.Disconnected)
        Log.d(TAG, "Disconnected")
    }

    // ── Kirim JSON frame ke Reverb ────────────────────────────────────────────

    private fun sendJson(obj: Any) {
        val json = gson.toJson(obj)
        Log.d(TAG, ">> $json")
        webSocket?.send(json)
    }

    /** Subscribe ke private channel — dipanggil setelah connection established. */
    private fun subscribeToChannel(chatId: Int) {
        val token = SessionManager.getToken() ?: return

        // 1. Minta auth token dari Laravel untuk private channel
        Thread {
            try {
                val channelName = "private-chat.$chatId"
                val authPayload = authenticateChannel(channelName, token)
                if (authPayload == null) {
                    _status.tryEmit(WebSocketStatus.Error("Gagal auth channel: $channelName"))
                    return@Thread
                }

                // 2. Subscribe setelah auth berhasil
                val subscribeMsg = mapOf(
                    "event" to "pusher:subscribe",
                    "data"  to mapOf(
                        "channel" to channelName,
                        "auth"    to authPayload
                    )
                )
                sendJson(subscribeMsg)

            } catch (e: Exception) {
                Log.e(TAG, "Subscribe error: ${e.message}")
                _status.tryEmit(WebSocketStatus.Error(e.message ?: "Subscribe failed"))
            }
        }.start()
    }

    /**
     * Panggil endpoint /broadcasting/auth di Laravel untuk mendapatkan
     * auth signature private channel.
     */
    private fun authenticateChannel(channelName: String, token: String): String? {
        return try {
            val baseUrl    = BuildConfig.BASE_URL.removeSuffix("api/")
            val authUrl    = "${baseUrl}broadcasting/auth"
            val socketIdSafe = socketId ?: return null

            val httpClient = OkHttpClient()
            val requestBody = okhttp3.FormBody.Builder()
                .add("socket_id",    socketIdSafe)
                .add("channel_name", channelName)
                .build()

            val request = Request.Builder()
                .url(authUrl)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .build()

            val response = httpClient.newCall(request).execute()
            val body     = response.body?.string()
            Log.d(TAG, "Auth response ($channelName): $body")

            if (!response.isSuccessful || body == null) return null

            // Laravel mengembalikan {"auth":"key:signature"}
            val json = gson.fromJson(body, JsonObject::class.java)
            json.get("auth")?.asString

        } catch (e: Exception) {
            Log.e(TAG, "Auth error: ${e.message}")
            null
        }
    }

    // ── WebSocket Listener ────────────────────────────────────────────────────

    private inner class ReverbListener(private val chatId: Int) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "WebSocket opened")
            _status.tryEmit(WebSocketStatus.Connected)
            // Tunggu event "pusher:connection_established" sebelum subscribe
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "<< $text")
            try {
                val event = gson.fromJson(text, ReverbEvent::class.java)
                handleEvent(event, chatId)
            } catch (e: Exception) {
                Log.e(TAG, "Parse error: ${e.message}")
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            onMessage(webSocket, bytes.utf8())
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket failure: ${t.message}")
            _status.tryEmit(WebSocketStatus.Error(t.message ?: "Connection failed"))
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "WebSocket closed: $reason")
            _status.tryEmit(WebSocketStatus.Disconnected)
        }
    }

    // ── Event Handler ─────────────────────────────────────────────────────────

    private fun handleEvent(event: ReverbEvent, chatId: Int) {
        when (event.event) {

            // Reverb memberikan socket_id setelah koneksi berhasil
            "pusher:connection_established" -> {
                val dataObj = gson.fromJson(event.data, JsonObject::class.java)
                socketId = dataObj.get("socket_id")?.asString
                Log.d(TAG, "Socket ID: $socketId")
                subscribeToChannel(chatId)
            }

            // Subscribe berhasil
            "pusher_internal:subscription_succeeded" -> {
                Log.d(TAG, "Subscribed to channel: ${event.channel}")
                _status.tryEmit(WebSocketStatus.Subscribed)
            }

            // Event pesan baru dari ChatController Laravel
            // Nama harus sama persis dengan broadcastAs() di App\Events\MessageSent.php
            "MessageSent" -> {
                event.data?.let { rawData ->
                    try {
                        // data bisa berupa JSON string atau JSON object
                        val dataStr = if (rawData.startsWith("{")) rawData
                                      else gson.fromJson(rawData, String::class.java)
                        val payload = gson.fromJson(dataStr, JsonObject::class.java)

                        // Ambil object "message" di dalam payload
                        val msgObj = payload.getAsJsonObject("message") ?: payload
                        val msgData = gson.fromJson(msgObj, ReverbMessageData::class.java)
                        Log.d(TAG, "New message: $msgData")
                        _incomingMessage.tryEmit(msgData)

                    } catch (e: Exception) {
                        Log.e(TAG, "Parse MessageSent error: ${e.message}")
                    }
                }
            }

            // Abaikan ping/pong internal
            "pusher:ping" -> sendJson(mapOf("event" to "pusher:pong"))

            else -> Log.d(TAG, "Unhandled event: ${event.event}")
        }
    }
}
