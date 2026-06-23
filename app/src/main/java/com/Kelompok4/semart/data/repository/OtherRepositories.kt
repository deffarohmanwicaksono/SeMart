package com.Kelompok4.semart.data.repository

import com.Kelompok4.semart.data.model.Notification
import com.Kelompok4.semart.data.model.Transaction
import com.Kelompok4.semart.data.remote.RetrofitClient

class NotificationRepository {

    private val api = RetrofitClient.instance

    suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            val response = api.getNotifications()
            if (response.isSuccessful) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat notifikasi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUnreadCount(): Result<Int> {
        return try {
            val response = api.getUnreadCount()
            if (response.isSuccessful) {
                Result.success(response.body()!!.unreadCount)
            } else {
                Result.failure(Exception("Gagal memuat jumlah notifikasi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class TransactionRepository {

    private val api = RetrofitClient.instance

    suspend fun getPurchaseHistory(): Result<List<Transaction>> {
        return try {
            val response = api.getPurchaseHistory()
            if (response.isSuccessful) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat riwayat: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun postReview(transactionId: Int, rating: Int, comment: String?): Result<String> {
        return try {
            val response = api.postReview(transactionId, com.Kelompok4.semart.data.remote.ReviewRequest(rating, comment))
            if (response.isSuccessful) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Gagal mengirim ulasan: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}