package com.Kelompok4.semart.features.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Konstanta Warna SeMart
val PrimaryBlue = Color(0xFF3B9DF8)
val DarkText = Color(0xFF243447)
val GrayText = Color(0xFF6B7280)
val BorderGray = Color(0xFFE5E7EB)
val SoftBlueBg = Color(0xFFF3F9FF)

// ── Data model ──
data class NotificationItem(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val body: String,
    val actionHint: String,
    val time: String,
    val isRead: Boolean
)

enum class NotificationType {
    WISHLIST, MESSAGE, PAYMENT, SYSTEM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    // Seed data
    val initialNotifications = remember {
        listOf(
            NotificationItem(
                id = 1,
                type = NotificationType.WISHLIST,
                title = "Pemberitahuan Stok Wishlist",
                body = "Produk \"Jaket Denim Pria\" yang ada di wishlist kamu telah terjual.",
                actionHint = "Lihat produk serupa dari seller lain",
                time = "5 menit lalu",
                isRead = false
            ),
            NotificationItem(
                id = 2,
                type = NotificationType.MESSAGE,
                title = "Pesan dari Andi",
                body = "Andi telah membalas pesanmu terkait \"Jaket Denim Pria\".",
                actionHint = "Buka ruang obrolan sekarang",
                time = "20 menit lalu",
                isRead = false
            ),
            NotificationItem(
                id = 3,
                type = NotificationType.PAYMENT,
                title = "Pembayaran Berhasil",
                body = "Pembayaranmu untuk \"Jaket Denim Pria\" telah berhasil diverifikasi.",
                actionHint = "Tunggu seller mengirimkan barang",
                time = "1 jam lalu",
                isRead = false
            ),
            NotificationItem(
                id = 4,
                type = NotificationType.SYSTEM,
                title = "Selamat Datang di SeMart!",
                body = "Akun kamu sudah aktif. Mulai jual-beli barang kebutuhan kuliah sekarang.",
                actionHint = "Jelajahi produk pilihan untukmu",
                time = "12 Jan 2024",
                isRead = true
            ),
            NotificationItem(
                id = 5,
                type = NotificationType.PAYMENT,
                title = "Transaksi Selesai",
                body = "Transaksi pembelian \"Buku Analisis Algoritma\" telah berhasil diselesaikan.",
                actionHint = "Berikan ulasan untuk penjual",
                time = "10 Jan 2024",
                isRead = true
            )
        )
    }

    var notifications by remember { mutableStateOf(initialNotifications) }
    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .statusBarsPadding()
                    .border(width = 1.dp, color = BorderGray, shape = RoundedCornerShape(0.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Panah kembali minimalis hitam
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

                    // Judul & Subjudul digabung di satu Column agar jaraknya rapat dan presisi
                    Column {
                        Text(
                            text = "Notifikasi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(2.dp)) // Kontrol jarak rapat antara judul & subjudul

                        Text(
                            text = if (unreadCount > 0) "$unreadCount belum dibaca" else "Semua sudah dibaca",
                            fontSize = 12.sp,
                            color = if (unreadCount > 0) PrimaryBlue else GrayText,
                            fontWeight = if (unreadCount > 0) FontWeight.Medium else FontWeight.Normal
                        )
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
                        NotifNavItem(selected = false, icon = Icons.Filled.Home, label = "Beranda", onClick = onHomeClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        NotifNavItem(selected = false, icon = Icons.Filled.Search, label = "Cari", onClick = onSearchClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        NotifNavItem(selected = false, icon = Icons.Filled.Chat, label = "Chat", onClick = onChatClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        NotifNavItem(selected = true, icon = Icons.Filled.Notifications, label = "Notifikasi")
                    }
                }
            }
        }
    ) { innerPadding ->

        if (notifications.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(SoftBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Belum ada notifikasi", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Aktivitas terbaru kamu akan muncul di sini.", fontSize = 13.sp, color = GrayText)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding),
                contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // Unread section
                if (unreadCount > 0) {
                    item {
                        Text(
                            text = "Baru",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkText,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    items(notifications.filter { !it.isRead }, key = { it.id }) { notif ->
                        NotificationCard(
                            notification = notif,
                            onClick = {
                                notifications = notifications.map {
                                    if (it.id == notif.id) it.copy(isRead = true) else it
                                }
                            }
                        )
                    }
                }

                // Read section
                val readList = notifications.filter { it.isRead }
                if (readList.isNotEmpty()) {
                    item {
                        Text(
                            text = "Sebelumnya",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkText,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    items(readList, key = { it.id }) { notif ->
                        NotificationCard(notification = notif, onClick = {})
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

// ── REUSABLE: Notification Card ──
@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    val icon = when (notification.type) {
        NotificationType.WISHLIST -> Icons.Outlined.Bookmark
        NotificationType.MESSAGE -> Icons.Outlined.MailOutline
        NotificationType.PAYMENT -> Icons.Outlined.Payments
        NotificationType.SYSTEM -> Icons.Outlined.Info
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (!notification.isRead) PrimaryBlue.copy(alpha = 0.3f) else BorderGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon bubble
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Unread dot indicator
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.body,
                    fontSize = 12.sp,
                    color = if (!notification.isRead) DarkText else GrayText,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.actionHint,
                        fontSize = 11.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = notification.time,
                        fontSize = 11.sp,
                        color = GrayText
                    )
                }
            }
        }
    }
}

// ── REUSABLE: Nav Item ──
@Composable
fun NotifNavItem(
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