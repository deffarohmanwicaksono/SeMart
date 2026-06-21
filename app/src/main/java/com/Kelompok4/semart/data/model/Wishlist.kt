package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class Wishlist(
    @SerializedName("wishlist_id") val wishlistId: Int,
    @SerializedName("product")     val product: WishlistProduct,
)

data class WishlistProduct(
    @SerializedName("id")          val id: Int,
    @SerializedName("name")        val name: String,
    @SerializedName("price")       val price: Double,
    @SerializedName("price_label") val priceLabel: String,
    // condition: "bekas_seperti_baru" | "bekas_baik" | "bekas_layak_pakai"
    @SerializedName("condition")   val condition: String,
    // status: "menunggu_verifikasi" | "dijual" | "sold_out" | "ditolak"
    @SerializedName("status")      val status: String,
    @SerializedName("category")    val category: String? = null,
    @SerializedName("image_url")   val imageUrl: String,
)