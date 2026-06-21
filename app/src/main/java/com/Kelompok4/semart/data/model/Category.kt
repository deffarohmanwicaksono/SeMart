package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")          val id: Int,
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String? = null,
)