package com.Kelompok4.semart.data.remote

import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.Kelompok4.semart.BuildConfig

// ── Session Manager — simpan & ambil token dari SharedPreferences ─────────────

object SessionManager {

    private const val PREF_NAME   = "semart_prefs"
    private const val KEY_TOKEN   = "auth_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_NAME    = "user_name"
    private const val KEY_EMAIL   = "user_email"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(token: String, userId: Int, name: String, email: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getToken(): String?   = prefs.getString(KEY_TOKEN, null)
    fun getUserId(): Int      = prefs.getInt(KEY_USER_ID, -1)
    fun getName(): String?    = prefs.getString(KEY_NAME, null)
    fun getEmail(): String?   = prefs.getString(KEY_EMAIL, null)
    fun isLoggedIn(): Boolean = getToken() != null

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

// ── Retrofit Client ───────────────────────────────────────────────────────────

object RetrofitClient {

    private const val BASE_URL = BuildConfig.BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val token = SessionManager.getToken()
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .apply {
                    if (token != null) addHeader("Authorization", "Bearer $token")
                }
                .build()
            chain.proceed(request)
        }
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}