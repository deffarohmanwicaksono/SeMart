package com.Kelompok4.semart.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────

    @POST("login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(): Response<MessageResponse>

    @GET("profile")
    suspend fun getProfile(): Response<ProfileResponse>

    // ── Produk ────────────────────────────────────────────────────────────────

    @GET("home")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("sort")     sort: String? = null,
    ): Response<ProductListResponse>

    @GET("search")
    suspend fun searchProducts(
        @Query("q")        keyword: String? = null,
        @Query("category") category: String? = null,
        @Query("sort")     sort: String? = null,
    ): Response<SearchResponse>

    @GET("products/{id}")
    suspend fun getProductDetail(
        @Path("id") id: Int
    ): Response<ProductDetailResponse>

    @GET("seller/{id}/profile")
    suspend fun getSellerProfile(
        @Path("id") id: Int
    ): Response<SellerProfileResponse>

    // ── Wishlist ──────────────────────────────────────────────────────────────

    @GET("wishlist")
    suspend fun getWishlist(): Response<WishlistResponse>

    @POST("wishlist")
    suspend fun toggleWishlist(
        @Body body: WishlistRequest
    ): Response<WishlistToggleResponse>

    @DELETE("wishlist/{productId}")
    suspend fun removeWishlist(
        @Path("productId") productId: Int
    ): Response<MessageResponse>

    @DELETE("wishlist-clear")
    suspend fun clearWishlist(): Response<MessageResponse>

    // ── Chat ──────────────────────────────────────────────────────────────────

    @GET("chat")
    suspend fun getChatList(): Response<ChatListResponse>

    @POST("chat")
    suspend fun createChat(
        @Body body: CreateChatRequest
    ): Response<CreateChatResponse>

    @GET("chat/{chatId}")
    suspend fun getChatSession(
        @Path("chatId") chatId: Int
    ): Response<ChatSessionResponse>

    @POST("chat/{chatId}/message")
    suspend fun sendMessage(
        @Path("chatId") chatId: Int,
        @Body body: SendMessageRequest,
    ): Response<SendMessageResponse>

    // ── Checkout ──────────────────────────────────────────────────────────────

    @GET("checkout/{token}")
    suspend fun getCheckoutDetail(
        @Path("token") token: String
    ): Response<CheckoutDetailResponse>

    @POST("checkout/{token}")
    suspend fun processCheckout(
        @Path("token") token: String,
        @Body body: CheckoutRequest,
    ): Response<CheckoutResponse>

    @Multipart
    @POST("checkout/{transactionId}/upload-proof")
    suspend fun uploadPaymentProof(
        @Path("transactionId") transactionId: Int,
        @Part proof: MultipartBody.Part,
    ): Response<UploadProofResponse>

    // ── Notifikasi ────────────────────────────────────────────────────────────

    @GET("notification")
    suspend fun getNotifications(): Response<NotificationListResponse>

    @GET("notification/count")
    suspend fun getUnreadCount(): Response<UnreadCountResponse>

    // ── Riwayat Transaksi ─────────────────────────────────────────────────────

    @GET("purchase-history")
    suspend fun getPurchaseHistory(): Response<TransactionListResponse>

    // ── Laporan ───────────────────────────────────────────────────────────────

    @POST("reports")
    suspend fun sendReport(
        @Body body: ReportRequest
    ): Response<MessageResponse>
}