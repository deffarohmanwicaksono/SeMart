package com.Kelompok4.semart.data.remote

import com.Kelompok4.semart.data.model.*
import com.google.gson.annotations.SerializedName

// ── Auth ──────────────────────────────────────────────────────────────────────

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String,
)

data class LoginResponse(
    @SerializedName("message") val message: String,
    @SerializedName("token")   val token: String,
    @SerializedName("user")    val user: User,
)

data class ProfileResponse(
    @SerializedName("id")           val id: Int,
    @SerializedName("name")         val name: String,
    @SerializedName("email")        val email: String,
    @SerializedName(value = "phone_number", alternate = ["nomor_hp", "no_hp", "phone"]) val phoneNumber: String? = null,
    @SerializedName("roles")        val roles: List<String> = emptyList(),
    @SerializedName("status")       val status: String? = null,
    @SerializedName("created_at")   val createdAt: String? = null,
)

data class MessageResponse(
    @SerializedName("message") val message: String,
)

// ── Product ───────────────────────────────────────────────────────────────────

data class ProductListResponse(
    @SerializedName("data") val data: List<Product>,
)

data class ProductDetailResponse(
    @SerializedName("data") val data: Product,
)

data class SearchResponse(
    @SerializedName("keyword") val keyword: String? = null,
    @SerializedName("data")    val data: List<Product>,
)

data class SellerProfileResponse(
    @SerializedName("seller")  val seller: SellerProfile,
    @SerializedName("reviews") val reviews: List<Review>,
)

data class SellerProfile(
    @SerializedName("id")            val id: Int,
    @SerializedName("name")          val name: String,
    @SerializedName("joined")        val joined: String? = null,
    @SerializedName("sold_count")    val soldCount: Int,
    @SerializedName("rating")        val rating: Double,
    @SerializedName("reviews_count") val reviewsCount: Int,
)

// ── Wishlist ──────────────────────────────────────────────────────────────────

data class WishlistResponse(
    @SerializedName("data") val data: List<Wishlist>,
)

data class WishlistRequest(
    @SerializedName("product_id") val productId: Int,
)

data class WishlistToggleResponse(
    @SerializedName("success")    val success: Boolean,
    @SerializedName("wishlisted") val wishlisted: Boolean,
    @SerializedName("message")    val message: String,
)

// ── Chat ──────────────────────────────────────────────────────────────────────

data class ChatListResponse(
    @SerializedName("data") val data: List<Chat>,
)

data class CreateChatRequest(
    @SerializedName("seller_id")  val sellerId: Int,
    @SerializedName("product_id") val productId: Int,
)

data class CreateChatResponse(
    @SerializedName("message") val message: String,
    @SerializedName("chat_id") val chatId: Int,
)

data class ChatSessionResponse(
    @SerializedName("chat")        val chat: Chat,
    @SerializedName("current_pov") val currentPov: String,
    @SerializedName("messages")    val messages: List<Message>,
)

data class SendMessageRequest(
    @SerializedName("message") val message: String,
)

data class SendMessageResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data")    val data: Message,
)

// ── Checkout ──────────────────────────────────────────────────────────────────

data class CheckoutDetailResponse(
    @SerializedName("is_valid")      val isValid: Boolean,
    @SerializedName("purchase_link") val purchaseLink: PurchaseLink,
    @SerializedName("product")       val product: CheckoutProduct,
    @SerializedName("seller")        val seller: CheckoutSeller,
    @SerializedName("payment_accounts") val paymentAccounts: List<PaymentAccountResponse>? = null
)

data class PaymentAccountResponse(
    @SerializedName("id")             val id: Int,
    @SerializedName("provider_name")  val providerName: String, // misal: "BCA", "GoPay"
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("account_name")   val accountName: String,
    @SerializedName("type")           val type: String // "bank" atau "e-wallet"
)

data class CheckoutProduct(
    @SerializedName("id")        val id: Int,
    @SerializedName("name")      val name: String,
    @SerializedName("image_url") val imageUrl: String,
)

data class CheckoutSeller(
    @SerializedName("id")   val id: Int,
    @SerializedName("name") val name: String,
)

data class CheckoutRequest(
    @SerializedName("payment_method") val paymentMethod: String,
)

data class CheckoutResponse(
    @SerializedName("message")        val message: String,
    @SerializedName("transaction_id") val transactionId: Int,
    @SerializedName("status")         val status: String,
    @SerializedName("total_price")    val totalPrice: Double,
    @SerializedName("price_label")    val priceLabel: String,
    @SerializedName("payment_method") val paymentMethod: String,
)

data class UploadProofResponse(
    @SerializedName("message")           val message: String,
    @SerializedName("transaction_id")    val transactionId: Int,
    @SerializedName("status")            val status: String,
    @SerializedName("payment_proof_url") val paymentProofUrl: String,
)

// ── Notification ──────────────────────────────────────────────────────────────

data class NotificationListResponse(
    @SerializedName("data")         val data: List<Notification>,
    @SerializedName("unread_count") val unreadCount: Int,
)

data class UnreadCountResponse(
    @SerializedName("unread_count") val unreadCount: Int,
)

// ── Transaction ───────────────────────────────────────────────────────────────

data class TransactionListResponse(
    @SerializedName("data") val data: List<Transaction>,
)

// ── Report ────────────────────────────────────────────────────────────────────

data class ReportRequest(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("reason")     val reason: String,
)