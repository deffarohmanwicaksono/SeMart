package com.Kelompok4.semart.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.Kelompok4.semart.features.auth.LoginScreen
import com.Kelompok4.semart.features.auth.SplashScreen
import com.Kelompok4.semart.features.home.HomeScreen
import com.Kelompok4.semart.features.search.SearchScreen
import com.Kelompok4.semart.features.product.ProductDetailScreen
import com.Kelompok4.semart.features.chat.ChatListScreen
import com.Kelompok4.semart.features.chat.ChatDetailScreen
import com.Kelompok4.semart.features.checkout.CheckoutScreen
import com.Kelompok4.semart.features.profile.ProfileScreen
import com.Kelompok4.semart.features.wishlist.WishlistScreen
import com.Kelompok4.semart.features.notification.NotificationScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable(route = "splash") {
            SplashScreen(navController = navController)
        }

        composable(route = "login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate(route = "home") {
                    popUpTo(route = "login") { inclusive = true }
                }
            })
        }

        composable(route = "home") {
            HomeScreen(
                onSearchClick = { navController.navigate("search") },
                onProductClick = { id -> navController.navigate("detail/$id") },
                onChatClick = { navController.navigate("chat") },
                onProfileClick = { navController.navigate("profile") },
                onWishlistClick = { navController.navigate("wishlist") },
                onNotificationClick = { navController.navigate("notification") }
            )
        }

        composable(route = "search") {
            SearchScreen(
                onBackClick = { navController.popBackStack() },
                onProductClick = { id -> navController.navigate("detail/$id") },
                onChatClick = { navController.navigate("chat") },
                onProfileClick = { navController.navigate("profile") }
            )
        }

        composable(route = "chat") {
            ChatListScreen(
                onBackToHome = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                onSearchClick = { navController.navigate("search") },
                onChatDetailClick = { id -> navController.navigate("chat_detail/$id") },
                onProfileClick = { navController.navigate("profile") }
            )
        }

        composable("checkout") {
            CheckoutScreen(onBackClick = { navController.popBackStack() })
        }

        composable(
            route = "detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("chat_detail/{chatId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("chatId")
            ChatDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToCheckout = { navController.navigate("checkout") }
            )
        }

        composable(route = "profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home")
                        launchSingleTop = true
                    }
                },
                onSearchClick = {
                    navController.navigate("search") {
                        launchSingleTop = true
                    }
                },
                onChatClick = {
                    navController.navigate("chat") {
                        launchSingleTop = true
                    }
                },

                onWishlistClick = { navController.navigate("wishlist") },
                onNotificationClick = { navController.navigate("notification") }
            )
        }

        composable("wishlist") {
            WishlistScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSearchClick = { navController.navigate("search") },
                onChatClick = { navController.navigate("chat") },
                onProductClick = { id -> navController.navigate("detail/$id") }
            )
        }

        composable("notification") {
            NotificationScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSearchClick = { navController.navigate("search") },
                onChatClick = { navController.navigate("chat") }
            )
        }
    }
}