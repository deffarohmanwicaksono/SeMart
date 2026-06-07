package com.Kelompok4.semart.features.wishlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Kelompok4.semart.R

// Konstanta Warna SeMart
val PrimaryBlue = Color(0xFF3B9DF8)
val DarkText = Color(0xFF243447)
val GrayText = Color(0xFF6B7280)
val BorderGray = Color(0xFFE5E7EB)
val SoftBlueBg = Color(0xFFF3F9FF)

// ── Data model ──
data class WishlistProduct(
    val id: Int,
    val name: String,
    val price: String,
    val condition: String,
    val imageResId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {}
) {
    val initialItems = remember {
        mutableStateListOf(
            WishlistProduct(1, "Jaket Denim Pria Slim Fit", "Rp 85.000", "Bekas Seperti Baru", R.drawable.login_illustration),
            WishlistProduct(2, "Kipas Angin Meja Sekai", "Rp 75.000", "Bekas Baik", R.drawable.login_illustration),
            WishlistProduct(3, "Buku Analisis Algoritma", "Rp 35.000", "Bekas Layak Pakai", R.drawable.login_illustration),
            WishlistProduct(4, "Meja Belajar Lipat Kayu", "Rp 45.000", "Bekas Baik", R.drawable.login_illustration),
            WishlistProduct(5, "Router Wifi TP-Link Bekas", "Rp 120.000", "Bekas Seperti Baru", R.drawable.login_illustration)
        )
    }

    var showRemoveDialog by remember { mutableStateOf<WishlistProduct?>(null) }

    // Remove confirmation dialog (Tombol konfirmasi diubah menjadi Biru)
    showRemoveDialog?.let { product ->
        AlertDialog(
            onDismissRequest = { showRemoveDialog = null },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Hapus dari Wishlist?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkText
                )
            },
            text = {
                Text(
                    text = "\"${product.name}\" akan dihapus dari wishlist kamu.",
                    fontSize = 13.sp,
                    color = GrayText,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        initialItems.remove(product)
                        showRemoveDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue), // Diubah ke Biru Utama
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Hapus", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = null }) {
                    Text("Batal", fontSize = 13.sp, color = GrayText)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White, // Perubahan 1: Background halaman jadi Putih
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 0.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Perubahan 2: Panah kembali biasa (Tanpa background/kotak & warna Hitam)
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = DarkText,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Wishlist Saya",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = if (initialItems.isNotEmpty()) "${initialItems.size} produk tersimpan" else "Belum ada produk",
                            fontSize = 12.sp,
                            color = if (initialItems.isNotEmpty()) PrimaryBlue else GrayText,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Perubahan 3: Ikon love merah di pojok kanan atas dihapus total
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGray, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        WishNavItem(selected = false, icon = Icons.Filled.Home, label = "Beranda", onClick = onHomeClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        WishNavItem(selected = false, icon = Icons.Filled.Search, label = "Cari", onClick = onSearchClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        WishNavItem(selected = false, icon = Icons.Filled.Chat, label = "Chat", onClick = onChatClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        WishNavItem(selected = false, icon = Icons.Filled.Person, label = "Profil")
                    }
                }
            }
        }
    ) { innerPadding ->

        if (initialItems.isEmpty()) {
            // ── Empty State ──
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(SoftBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Wishlist Kosong",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Produk yang kamu simpan akan muncul di sini. Mulai jelajahi dan simpan barang favoritmu!",
                        fontSize = 13.sp,
                        color = GrayText,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onSearchClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(46.dp)
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Jelajahi Produk", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 20.dp)
            ) {
                items(initialItems, key = { it.id }) { product ->
                    WishlistCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onRemove = { showRemoveDialog = product }
                    )
                }
            }
        }
    }
}

// ── Perubahan 4: Wishlist Card Meniru Product Card Lain (Tanpa seller, tanpa tombol chat, X di kanan bawah warna Biru) ──
@Composable
fun WishlistCard(
    product: WishlistProduct,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.clickable { onClick() }) {
                // Product image area
                Image(
                    painter = painterResource(id = product.imageResId),
                    contentDescription = "Foto Produk",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(Color(0xFFF8FAFC)),
                    contentScale = ContentScale.Fit
                )

                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = product.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = product.price,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Condition chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoftBlueBg)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = product.condition,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Tombol X (Hapus) diletakkan di Pojok Kanan Bawah, Berwarna Biru dengan background soft blue
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F9FF))
                    .clickable { onRemove() }
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Hapus dari Wishlist",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ── REUSABLE: Nav Item ──
@Composable
fun WishNavItem(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(height = 64.dp, width = 62.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) PrimaryBlue else Color(0xFFA1ACB8),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (selected) PrimaryBlue else GrayText,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}