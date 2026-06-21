package com.Kelompok4.semart.data.repository

import com.Kelompok4.semart.data.model.Wishlist
import com.Kelompok4.semart.data.remote.RetrofitClient
import com.Kelompok4.semart.data.remote.WishlistRequest

class WishlistRepository {

    private val api = RetrofitClient.instance

    suspend fun getWishlist(): Result<List<Wishlist>> {
        return try {
            val response = api.getWishlist()
            if (response.isSuccessful) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat wishlist: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleWishlist(productId: Int): Result<Boolean> {
        return try {
            val response = api.toggleWishlist(WishlistRequest(productId))
            if (response.isSuccessful) {
                Result.success(response.body()!!.wishlisted)
            } else {
                Result.failure(Exception("Gagal update wishlist: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeWishlist(productId: Int): Result<String> {
        return try {
            val response = api.removeWishlist(productId)
            if (response.isSuccessful) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Gagal menghapus wishlist: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearWishlist(): Result<String> {
        return try {
            val response = api.clearWishlist()
            if (response.isSuccessful) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Gagal mengosongkan wishlist: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}