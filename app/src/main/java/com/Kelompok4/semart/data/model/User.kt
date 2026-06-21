package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")           val id: Int,
    @SerializedName("name")         val name: String,
    @SerializedName("email")        val email: String,
    @SerializedName(value = "phone_number", alternate = ["nomor_hp", "no_hp", "phone"]) val phoneNumber: String? = null,
    @SerializedName("roles")        val roles: List<String> = emptyList(),
    @SerializedName("status")       val status: String? = null, // "aktif" | "diblokir"
    @SerializedName("created_at")   val createdAt: String? = null,
)