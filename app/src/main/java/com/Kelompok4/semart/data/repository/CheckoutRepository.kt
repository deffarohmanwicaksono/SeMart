package com.Kelompok4.semart.data.repository

import com.Kelompok4.semart.data.remote.CheckoutDetailResponse
import com.Kelompok4.semart.data.remote.CheckoutRequest
import com.Kelompok4.semart.data.remote.CheckoutResponse
import com.Kelompok4.semart.data.remote.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class CheckoutRepository {

    private val api = RetrofitClient.instance

    suspend fun getCheckoutDetail(token: String): Result<CheckoutDetailResponse> {
        return try {
            val response = api.getCheckoutDetail(token)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Link tidak valid: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun processCheckout(
        token: String,
        paymentMethod: String,
    ): Result<CheckoutResponse> {
        return try {
            val response = api.processCheckout(token, CheckoutRequest(paymentMethod))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Checkout gagal: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadPaymentProof(
        transactionId: Int,
        imageFile: File,
    ): Result<String> {
        return try {
            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("payment_proof", imageFile.name, requestBody)
            val response = api.uploadPaymentProof(transactionId, part)
            if (response.isSuccessful) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("Upload gagal: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}