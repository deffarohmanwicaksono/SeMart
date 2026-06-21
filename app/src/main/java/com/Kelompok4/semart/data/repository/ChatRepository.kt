package com.Kelompok4.semart.data.repository

import com.Kelompok4.semart.data.model.Chat
import com.Kelompok4.semart.data.model.Message
import com.Kelompok4.semart.data.remote.ChatSessionResponse
import com.Kelompok4.semart.data.remote.CreateChatRequest
import com.Kelompok4.semart.data.remote.RetrofitClient
import com.Kelompok4.semart.data.remote.SendMessageRequest

class ChatRepository {

    private val api = RetrofitClient.instance

    suspend fun getChatList(): Result<List<Chat>> {
        return try {
            val response = api.getChatList()
            if (response.isSuccessful) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat chat: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createChat(sellerId: Int, productId: Int): Result<Int> {
        return try {
            val response = api.createChat(CreateChatRequest(sellerId, productId))
            if (response.isSuccessful) {
                Result.success(response.body()!!.chatId)
            } else {
                Result.failure(Exception("Gagal membuat chat: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChatSession(chatId: Int): Result<ChatSessionResponse> {
        return try {
            val response = api.getChatSession(chatId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal memuat sesi chat: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(chatId: Int, message: String): Result<Message> {
        return try {
            val response = api.sendMessage(chatId, SendMessageRequest(message))
            if (response.isSuccessful) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mengirim pesan: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}