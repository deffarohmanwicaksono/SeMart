package com.Kelompok4.semart.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.Kelompok4.semart.features.auth.LoginScreen
import com.Kelompok4.semart.features.auth.SplashScreen
import com.Kelompok4.semart.features.chat.ChatDetailScreen
import com.Kelompok4.semart.features.chat.ChatListScreen
import com.Kelompok4.semart.features.checkout.CheckoutScreen
import com.Kelompok4.semart.features.home.HomeScreen
import com.Kelompok4.semart.features.notification.NotificationScreen
import com.Kelompok4.semart.features.product.ProductDetailScreen
import com.Kelompok4.semart.features.profile.ProfileScreen
import com.Kelompok4.semart.features.search.SearchScreen
import com.Kelompok4.semart.features.transaction.TransactionHistoryScreen
import com.Kelompok4.semart.features.wishlist.WishlistScreen
import com.Kelompok4.semart.features.chat.ChatViewModel
import com.Kelompok4.semart.features.profile.SellerProfileScreen
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ── Splash ────────────────────────────────────────────────────────────
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // ── Login ─────────────────────────────────────────────────────────────
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Home ──────────────────────────────────────────────────────────────
        composable(route = Screen.Home.route) {
            HomeScreen(
                onSearchClick      = { navController.navigate(Screen.Search.route) },
                onProductClick     = { id -> navController.navigate(Screen.productDetail(id)) },
                onChatClick        = { navController.navigate(Screen.ChatList.route) },
                onProfileClick     = { navController.navigate(Screen.Profile.route) },
                onWishlistClick    = { navController.navigate(Screen.Wishlist.route) },
                onNotificationClick = { navController.navigate(Screen.Notification.route) }
            )
        }

        // ── Search ────────────────────────────────────────────────────────────
        composable(route = Screen.Search.route) {
            SearchScreen(
                onBackClick    = { navController.popBackStack() },
                onHomeClick    = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                },
                onProductClick = { id -> navController.navigate(Screen.productDetail(id)) },
                onChatClick    = { navController.navigate(Screen.ChatList.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        }

        // ── Product Detail ────────────────────────────────────────────────────
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val chatViewModel: ChatViewModel = viewModel()
            ProductDetailScreen(
                productId  = productId,
                onBackClick = { navController.popBackStack() },
                onChatClick = { sellerId, productIdArg ->
                    chatViewModel.createChat(sellerId, productIdArg) { chatId ->
                        navController.navigate(Screen.chatDetail(chatId))
                    }
                },
                onSellerClick = { sellerId ->
                    navController.navigate(Screen.sellerProfile(sellerId))
                }
            )
        }

        // ── Chat List ─────────────────────────────────────────────────────────
        composable(route = Screen.ChatList.route) {
            ChatListScreen(
                onBackToHome     = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                },
                onSearchClick    = { navController.navigate(Screen.Search.route) },
                onChatDetailClick = { id -> navController.navigate(Screen.chatDetail(id)) },
                onProfileClick   = { navController.navigate(Screen.Profile.route) }
            )
        }

        // ── Chat Detail ───────────────────────────────────────────────────────
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(navArgument("chatId") { type = NavType.IntType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getInt("chatId") ?: 0
            ChatDetailScreen(
                chatId     = chatId,
                onBackClick = { navController.popBackStack() },
                onNavigateToCheckout = { token ->
                    navController.navigate(Screen.checkout(token))
                }
            )
        }

        // ── Checkout ──────────────────────────────────────────────────────────
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            CheckoutScreen(
                token      = token,
                onBackClick = { navController.popBackStack() },
                onCheckoutSuccess = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // ── Wishlist ──────────────────────────────────────────────────────────
        composable(route = Screen.Wishlist.route) {
            WishlistScreen(
                onBackClick    = { navController.popBackStack() },
                onSearchClick  = { navController.navigate(Screen.Search.route) },
                onProductClick = { id -> navController.navigate(Screen.productDetail(id)) }
            )
        }

        // ── Notification ──────────────────────────────────────────────────────
        composable(route = Screen.Notification.route) {
            NotificationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Transaction History ───────────────────────────────────────────────
        composable(route = Screen.History.route) {
            TransactionHistoryScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Profile ───────────────────────────────────────────────────────────
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onBackClick  = { navController.popBackStack() },
                onHomeClick  = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route) {
                        launchSingleTop = true
                    }
                },
                onChatClick  = {
                    navController.navigate(Screen.ChatList.route) {
                        launchSingleTop = true
                    }
                },
                onWishlistClick          = { navController.navigate(Screen.Wishlist.route) },
                onNotificationClick      = { navController.navigate(Screen.Notification.route) },
                onTransactionHistoryClick = { navController.navigate(Screen.History.route) },
                onLogoutClick = {
                    com.Kelompok4.semart.data.remote.SessionManager.clearSession()
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Seller Profile ────────────────────────────────────────────────────
        composable(
            route = Screen.SellerProfile.route,
            arguments = listOf(navArgument("sellerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sellerId = backStackEntry.arguments?.getInt("sellerId") ?: 0
            SellerProfileScreen(sellerId = sellerId, onBackClick = { navController.popBackStack() })
        }
    }
}