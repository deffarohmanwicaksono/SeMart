package com.Kelompok4.semart.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")                    val id: Int,
    @SerializedName("name")                  val name: String,
    @SerializedName("description")           val description: String? = null,
    @SerializedName("price")                 val price: Double,
    @SerializedName("price_label")           val priceLabel: String,
    // condition: "bekas_seperti_baru" | "bekas_baik" | "bekas_layak_pakai"
    @SerializedName("condition")             val condition: String,
    // status: "menunggu_verifikasi" | "dijual" | "sold_out" | "ditolak"
    @SerializedName("status")               val status: String,
    @SerializedName("stock")                val stock: Int = 1,
    @SerializedName("category")             val category: String? = null,
    @SerializedName("seller")              val seller: SellerInfo? = null,
    @SerializedName("images")              val images: List<ProductImage> = emptyList(),
    @SerializedName("is_wishlisted")        val isWishlisted: Boolean = false,
    @SerializedName("created_at")           val createdAt: String? = null,
    // Hanya ada di response detail produk
    @SerializedName("seller_rating")        val sellerRating: Double? = null,
    @SerializedName("seller_reviews_count") val sellerReviewsCount: Int? = null,
)

data class SellerInfo(
    @SerializedName("id")   val id: Int,
    @SerializedName("name") val name: String,
)