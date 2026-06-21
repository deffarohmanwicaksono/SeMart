package com.Kelompok4.semart.navigation

sealed class Screen(val route: String) {
    object Splash          : Screen("splash")
    object Login           : Screen("login")
    object Home            : Screen("home")
    object Search          : Screen("search")
    object ProductDetail   : Screen("detail/{productId}")
    object Wishlist        : Screen("wishlist")
    object Checkout        : Screen("checkout/{token}")
    object ChatList        : Screen("chat")
    object ChatDetail      : Screen("chat_detail/{chatId}")
    object Notification    : Screen("notification")
    object History         : Screen("history")
    object Profile         : Screen("profile")
    object SellerProfile   : Screen("seller_profile/{sellerId}")

    // Helper untuk generate route dengan argument
    companion object {
        fun productDetail(productId: Int) = "detail/$productId"
        fun chatDetail(chatId: Int)       = "chat_detail/$chatId"
        fun sellerProfile(sellerId: Int)  = "seller_profile/$sellerId"
        fun checkout(token: String)       = "checkout/$token"
    }
}