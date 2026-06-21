package com.Kelompok4.semart.data.repository

import com.Kelompok4.semart.data.model.Product
import com.Kelompok4.semart.data.remote.ReportRequest
import com.Kelompok4.semart.data.remote.RetrofitClient
import com.Kelompok4.semart.data.remote.SellerProfileResponse

class ProductRepository {

    private val api = RetrofitClient.instance

    suspend fun getProducts(
        category: String? = null,
        sort: String? = null,
    ): Result<List<Product>> {
        return try {
            val response = api.getProducts(category, sort)
            if (response.isSuccessful) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal memuat produk: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(
        keyword: String? = null,
        category: String? = null,
        sort: String? = null,
    ): Result<List<Product>> {
        return try {
            val response = api.searchProducts(keyword, category, sort)
            if (response.isSuccessful) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Gagal mencari produk: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductDetail(id: Int): Result<Product> {
        return try {
            val response = api.getProductDetail(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Produk tidak ditemukan: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSellerProfile(id: Int): Result<SellerProfileResponse> {
        return try {
            val response = api.getSellerProfile(id)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal memuat profil seller: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendReport(productId: Int, reason: String): Result<String> {
        return try {
            val response = api.sendReport(ReportRequest(productId, reason))
            if (response.isSuccessful) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Gagal mengirim laporan: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}