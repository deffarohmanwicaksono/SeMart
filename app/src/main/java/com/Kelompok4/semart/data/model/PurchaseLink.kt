package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class PurchaseLink(
    @SerializedName("token")            val token: String,
    @SerializedName("deal_price")       val dealPrice: Double,
    @SerializedName("price_label")      val priceLabel: String,
    @SerializedName("expired_at")       val expiredAt: String? = null,
    @SerializedName("note")             val note: String? = null,
    // payment_methods di DB adalah kolom json → List<String>
    @SerializedName("payment_methods")  val paymentMethods: Any? = null,
    @SerializedName("is_used")          val isUsed: Boolean = false,
)