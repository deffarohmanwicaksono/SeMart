package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class ProductImage(
    @SerializedName("id")    val id: Int,
    @SerializedName("url")   val url: String,
    @SerializedName("order") val order: Int = 0,
)