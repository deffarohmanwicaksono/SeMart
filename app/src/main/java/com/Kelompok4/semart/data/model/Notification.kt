package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("id")          val id: Int,
    @SerializedName("type")        val type: String,
    @SerializedName("title")       val title: String,
    // Di DB kolom namanya "content", tapi API controller sudah map ke "message"
    @SerializedName("message")     val message: String,
    @SerializedName("time")        val time: String,
    @SerializedName("is_unread")   val isUnread: Boolean,
    @SerializedName("icon")        val icon: String? = null,
    @SerializedName("color_class") val colorClass: String? = null,
    @SerializedName("link")        val link: String? = null,
)