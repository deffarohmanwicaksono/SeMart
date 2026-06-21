package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

// Di Message.kt

data class Message(
    @SerializedName("id")               val id: Int,
    @SerializedName("sender_id")        val senderId: Int,
    @SerializedName("sender_name")      val senderName: String? = null,
    @SerializedName("is_purchase_link") val isPurchaseLink: Any? = false,
    @SerializedName("message")          val message: String? = null,
    @SerializedName("purchase_link")    val purchaseLink: PurchaseLinkMessage? = null,
    @SerializedName("created_at")       val createdAt: String? = null,
) {
    val isLink: Boolean get() = when(isPurchaseLink) {
        is Boolean -> isPurchaseLink
        is Number -> isPurchaseLink.toInt() == 1
        is String -> isPurchaseLink == "1" || isPurchaseLink.lowercase() == "true"
        else -> false
    }
}

data class PurchaseLinkMessage(
    @SerializedName("token")            val token: String? = null,
    @SerializedName("deal_price")       val dealPrice: Double? = null,
    @SerializedName("price_label")      val priceLabel: String? = null,
    @SerializedName("expired_at")       val expiredAt: String? = null,
    @SerializedName("is_used")          val isUsed: Any? = false,
    @SerializedName("is_valid")         val isValid: Any? = true,
    @SerializedName("note")             val note: String? = null,
    @SerializedName("payment_methods")  val paymentMethods: Any? = null,
    @SerializedName("checkout_url")     val checkoutUrl: String? = null,
) {
    val used: Boolean get() = when(isUsed) {
        is Boolean -> isUsed
        is Number -> isUsed.toInt() == 1
        else -> false
    }
    val valid: Boolean get() = when(isValid) {
        is Boolean -> isValid
        is Number -> isValid.toInt() == 1
        else -> true
    }
}