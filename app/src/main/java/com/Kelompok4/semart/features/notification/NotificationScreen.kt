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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Kelompok4.semart.ui.theme.*
import com.Kelompok4.semart.features.notification.NotificationViewModel
import com.Kelompok4.semart.features.notification.NotificationState

// ── Data model ──
data class NotificationItemUI(
    val id: Int,
    val type: NotificationTypeUI,
    val title: String,
    val body: String,
    val actionHint: String,
    val time: String,
    val isRead: Boolean
)

enum class NotificationTypeUI {
    WISHLIST, MESSAGE, PAYMENT, SYSTEM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
        viewModel.fetchUnreadCount()
    }

    val notifications = remember(state) {
        if (state is NotificationState.Success) {
            (state as NotificationState.Success).notifications.map { notif ->
                val typeUI = when (notif.type) {
                    "wishlist" -> NotificationTypeUI.WISHLIST
                    "chat" -> NotificationTypeUI.MESSAGE
                    "transaction" -> NotificationTypeUI.PAYMENT
                    else -> NotificationTypeUI.SYSTEM
                }
                val actionHint = when (notif.type) {
                    "wishlist" -> "Lihat produk serupa dari seller lain"
                    "chat" -> "Buka ruang obrolan sekarang"
                    "transaction" -> "Cek status transaksi"
                    else -> "Jelajahi SeMart"
                }
                NotificationItemUI(
                    id = notif.id,
                    type = typeUI,
                    title = notif.title,
                    body = notif.message,
                    actionHint = actionHint,
                    time = notif.time,
                    isRead = !notif.isUnread
                )
            }
        } else emptyList()
    }

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
                    // Panah kembali
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

                    Column {
                        Text(
                            text = "Notifikasi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = if (unreadCount > 0) "$unreadCount belum dibaca" else "Semua sudah dibaca",
                            fontSize = 12.sp,
                            color = if (unreadCount > 0) PrimaryBlue else GrayText,
                            fontWeight = if (unreadCount > 0) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        if (state is NotificationState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (state is NotificationState.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text((state as NotificationState.Error).message, color = Color.Red)
            }
        } else if (notifications.isEmpty()) {
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
    notification: NotificationItemUI,
    onClick: () -> Unit
) {
    val icon = when (notification.type) {
        NotificationTypeUI.WISHLIST -> Icons.Outlined.Bookmark
        NotificationTypeUI.MESSAGE -> Icons.Outlined.MailOutline
        NotificationTypeUI.PAYMENT -> Icons.Outlined.Payments
        NotificationTypeUI.SYSTEM -> Icons.Outlined.Info
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