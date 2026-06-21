package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class Chat(
    @SerializedName("id")             val id: Int,
    @SerializedName("product")        val product: ChatProduct,
    @SerializedName("buyer")          val buyer: ChatUser,
    @SerializedName("seller")         val seller: ChatUser,
    // "buyer" atau "seller" — ditentukan dari sisi siapa yang login
    @SerializedName("current_pov")    val currentPov: String,
    @SerializedName("latest_message") val latestMessage: LatestMessage? = null,
    @SerializedName("updated_at")     val updatedAt: String? = null,
)

data class ChatProduct(
    @SerializedName("id")        val id: Int,
    @SerializedName("name")      val name: String,
    @SerializedName("image_url") val imageUrl: String,
)

data class ChatUser(
    @SerializedName("id")   val id: Int,
    @SerializedName("name") val name: String,
)

data class LatestMessage(
    @SerializedName("message")    val message: String,
    @SerializedName("created_at") val createdAt: String? = null,
)