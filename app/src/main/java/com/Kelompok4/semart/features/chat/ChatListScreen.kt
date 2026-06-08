package com.Kelompok4.semart.features.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

data class ChatItemData(
    val id: Int,
    val senderName: String,
    val productName: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int,
    val imageResId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onBackToHome: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onChatDetailClick: (Int) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val chatList = remember {
        listOf(
            ChatItemData(1, "Andi Pratama", "Kipas Angin Meja Sekai", "Apakah barangnya masih ready kak?", "14:30", 1, R.drawable.login_illustration),
            ChatItemData(2, "Siti Aisyah", "Buku Analisis Algoritma", "Bisa nego tipis gak kak?", "10:15", 0, R.drawable.login_illustration),
            ChatItemData(3, "Dewi Lestari", "Meja Belajar Lipat", "Lokasi COD-nya di mana ya?", "09:45", 2, R.drawable.login_illustration),
            ChatItemData(4, "Budi Santoso", "Router Wifi Bekas", "Barangnya masih mulus kak?", "Kemarin", 0, R.drawable.login_illustration)
        )
    }

    // Hitung total pesan yang belum dibaca secara dinamis
    val totalUnread = remember(chatList) { chatList.sumOf { it.unreadCount } }

    val filteredChatList = chatList.filter {
        it.senderName.contains(searchQuery, ignoreCase = true) ||
                it.productName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Surface(
                color = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 10.dp)
                ) {
                    // JUDUL UTAMA
                    Text(
                        text = "Chat",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Ada $totalUnread pesan yang belum dibaca",
                            fontSize = 12.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    } else {
                        Text(
                            text = "Menampilkan hasil pencarian",
                            fontSize = 12.sp,
                            color = GrayText,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SEARCH BAR
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    color = DarkText,
                                    fontSize = 13.sp
                                ),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Cari penjual atau barang...",
                                            color = GrayText,
                                            fontSize = 13.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Clear",
                                        tint = GrayText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SoftBlueBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.List,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        BorderGray,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround, // Menyebar menu secara merata
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Menu Beranda
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ChatNavItemManual(selected = false, icon = Icons.Filled.Home, label = "Beranda", onClick = onBackToHome)
                    }

                    // Menu Cari
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ChatNavItemManual(selected = false, icon = Icons.Filled.Search, label = "Cari", onClick = onSearchClick)
                    }

                    // Menu Chat (Aktif - Tombol Jual Dihapus)
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ChatNavItemManual(selected = true, icon = Icons.Filled.Chat, label = "Chat")
                    }

                    // Menu Profil
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ChatNavItemManual(selected = false, icon = Icons.Filled.Person, label = "Profil", onClick = onProfileClick)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Render List Chat secara Dinamis
            if (filteredChatList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada percakapan ditemukan.", color = GrayText, fontSize = 13.sp)
                    }
                }
            } else {
                items(filteredChatList) { chat ->
                    ChatRowItem(
                        chatData = chat,
                        onClick = { onChatDetailClick(chat.id) }
                    )
                }
            }

            // Info Card Box
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftBlueBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6E9FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Badge angka menunjukkan jumlah pesan yang belum dibaca.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = DarkText,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pesan yang masuk terlebih dahulu akan ditampilkan paling atas.",
                                fontSize = 12.sp,
                                color = GrayText,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ChatRowItem(
    chatData: ChatItemData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = chatData.imageResId),
                contentDescription = "Foto Produk",
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8FAFC)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = chatData.senderName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = chatData.productName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = chatData.lastMessage,
                    fontSize = 12.sp,
                    color = GrayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = chatData.time,
                    fontSize = 11.sp,
                    color = GrayText
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (chatData.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chatData.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Buka Chat",
                tint = Color(0xFFA1ACB8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun ChatNavItemManual(
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