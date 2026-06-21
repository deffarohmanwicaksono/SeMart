package com.Kelompok4.semart.data.repository

import com.Kelompok4.semart.data.remote.LoginRequest
import com.Kelompok4.semart.data.remote.LoginResponse
import com.Kelompok4.semart.data.remote.RetrofitClient
import com.Kelompok4.semart.data.remote.SessionManager

class AuthRepository {

    private val api = RetrofitClient.instance

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                SessionManager.saveSession(
                    token  = body.token,
                    userId = body.user.id,
                    name   = body.user.name,
                    email  = body.user.email,
                )
                Result.success(body)
            } else {
                Result.failure(Exception("Login gagal: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<String> {
        return try {
            val response = api.logout()
            SessionManager.clearSession()
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Logout berhasil.")
            } else {
                Result.success("Logout berhasil.")
            }
        } catch (e: Exception) {
            SessionManager.clearSession()
            Result.success("Logout berhasil.")
        }
    }

    suspend fun getProfile(): Result<com.Kelompok4.semart.data.remote.ProfileResponse> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Gagal memuat profil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}