package com.Kelompok4.semart.features.product

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Flag
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

// Konstanta Warna
val PrimaryBlue = Color(0xFF3B9DF8)
val DarkText = Color(0xFF243447)
val GrayText = Color(0xFF6B7280)
val BorderGray = Color(0xFFE5E7EB)
val SoftBlueBg = Color(0xFFF3F9FF)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onBackClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(pageCount = { 6 })
    var isFavorite by remember { mutableStateOf(false) }

    // Data Mockup
    val title = "Laptop MacBook Air M1 2020"
    val price = "Rp 7.500.000"
    val condition = "Bekas Baik"
    val brand = "Apple"
    val year = "2021"
    val description = "MacBook Air M1 2020, performa masih kencang untuk kebutuhan kuliah, browsing, desain ringan, dan editing. Baterai awet, tidak pernah servis, semua fungsi normal. Kelengkapan: charger original + tas sleeve."

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = DarkText)
                    }
                },
                actions = {
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // ACTION BUTTONS BOTTOM BAR
            Surface(
                color = Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier.border(1.dp, Color(0xFFF1F5F9))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = { /* Aksi Chat */ },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chat dengan Seller", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Tombol Simpan Wishlist
                        OutlinedButton(
                            onClick = { isFavorite = !isFavorite },
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simpan Wishlist", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        // Tombol Laporkan Produk
                        OutlinedButton(
                            onClick = { /* Laporkan */ },
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(Icons.Outlined.Flag, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Laporkan Produk", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // IMAGE SLIDER PAGER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color(0xFFF8FAFC))
            ) {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    Image(
                        painter = painterResource(id = R.drawable.login_illustration),
                        contentDescription = "Foto Produk",
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(6) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) PrimaryBlue else Color(0xFFCBD5E1))
                        )
                    }
                }
            }

            // INFO UTAMA
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SoftBlueBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Elektronik", color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText, lineHeight = 26.sp)

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = price, fontSize = 22.sp, fontWeight = FontWeight.Black, color = PrimaryBlue)

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE6F4EA))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(condition, color = Color(0xFF137333), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // GRID
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item 1: Kondisi
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.AccessTime, null, tint = DarkText, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(condition, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text("Kondisi", fontSize = 11.sp, color = GrayText)
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE2E8F0)))

                    // Item 2: Merek
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.Info, null, tint = DarkText, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(brand, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text("Merek", fontSize = 11.sp, color = GrayText)
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE2E8F0)))

                    // Item 3: Tahun
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.CalendarMonth, null, tint = DarkText, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(year, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text("Tahun", fontSize = 11.sp, color = GrayText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DESKRIPSI PRODUK
                Text("Deskripsi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = description, fontSize = 13.sp, color = DarkText, lineHeight = 20.sp)

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Aksi Selengkapnya */ }
                ) {
                    Text("Lihat selengkapnya", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // KARTU PROFIL SELLER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .clickable { /* Buka Profil Penjual */ }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AP", fontWeight = FontWeight.Bold, color = DarkText, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Andi Pratama", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Filled.Star, contentDescription = "Verified", tint = PrimaryBlue, modifier = Modifier.size(15.dp))
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("4.9", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Spacer(modifier = Modifier.width(4.dp))
                            repeat(5) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("(128 ulasan)", fontSize = 11.sp, color = GrayText)
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Kampus UNS", fontSize = 12.sp, color = GrayText)
                    }

                    Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = GrayText)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}