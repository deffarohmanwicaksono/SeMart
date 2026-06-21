package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id")                val id: Int,
    @SerializedName("transaction_code")  val transactionCode: String? = null,
    @SerializedName("product")           val product: TransactionProduct,
    @SerializedName("seller")            val seller: TransactionUser,
    @SerializedName("buyer")             val buyer: TransactionUser,
    @SerializedName("quantity")          val quantity: Int = 1,
    @SerializedName("total_price")       val totalPrice: Double,
    @SerializedName("price_label")       val priceLabel: String,
    // status: "menunggu_pembayaran" | "dibayar" | "selesai" | "gagal"
    @SerializedName("status")            val status: String,
    @SerializedName("status_label")      val statusLabel: String,
    @SerializedName("status_class")      val statusClass: String,
    @SerializedName("payment_method")    val paymentMethod: String? = null,
    @SerializedName("payment_proof_url") val paymentProofUrl: String? = null,
    @SerializedName("date")              val date: String? = null,
    @SerializedName("date_label")        val dateLabel: String? = null,
    @SerializedName("time_label")        val timeLabel: String? = null,
    @SerializedName("paid_at")           val paidAt: String? = null,
    @SerializedName("completed_at")      val completedAt: String? = null,
)

data class TransactionProduct(
    @SerializedName("id")        val id: Int,
    @SerializedName("name")      val name: String,
    @SerializedName("image_url") val imageUrl: String,
)

data class TransactionUser(
    @SerializedName("id")   val id: Int?,
    @SerializedName("name") val name: String?,
)