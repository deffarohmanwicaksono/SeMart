package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class Report(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("reason")     val reason: String,
    // status: "menunggu" | "ditindaklanjuti" | "ditolak"
    @SerializedName("status")     val status: String? = null,
)