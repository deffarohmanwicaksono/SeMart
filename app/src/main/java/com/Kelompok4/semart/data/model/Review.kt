package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("buyer_name")   val buyerName: String? = null,
    @SerializedName("product_name") val productName: String? = null,
    // rating di DB adalah tinyInteger (1-5)
    @SerializedName("rating")       val rating: Int,
    @SerializedName("comment")      val comment: String? = null,
    @SerializedName("created_at")   val createdAt: String? = null,
)