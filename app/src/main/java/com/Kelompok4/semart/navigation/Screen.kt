package com.Kelompok4.semart.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Search : Screen("search")
    object ProductDetail : Screen("product_detail")
    object Wishlist : Screen("wishlist")
    object Checkout : Screen("checkout")
    object ChatList : Screen("chat_list")
    object ChatSession : Screen("chat_session")
    object Notification : Screen("notification")
    object Transaction : Screen("transaction")
    object RatingReview : Screen("rating_review")
    object Profile : Screen("profile")
    object SellerProfile : Screen("seller_profile")
}