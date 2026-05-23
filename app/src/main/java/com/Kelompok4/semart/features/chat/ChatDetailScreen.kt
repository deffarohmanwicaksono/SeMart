package com.Kelompok4.semart.features.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================
// MODEL DATA & ENUM
// ==========================================
enum class ChatRowType {
    TEXT_MESSAGE,
    ACTIVE_LINK,
    EXPIRED_LINK
}

data class MessageData(
    val id: Int,
    val text: String,
    val time: String,
    val isUser: Boolean,
    val type: ChatRowType = ChatRowType.TEXT_MESSAGE
)

fun getBubbleShape(isUser: Boolean): RoundedCornerShape {
    return if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    }
}

// ==========================================
// HALAMAN UTAMA: CHAT DETAIL SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    onBackClick: () -> Unit,
    onNavigateToCheckout: () -> Unit) {
    var messageText by remember { mutableStateOf("") }

    val conversation = remember {
        mutableStateListOf(
            MessageData(1, "Halo, apakah Jaket Denim Pria nya masih ada kak?", "14:30", isUser = true),
            MessageData(2, "Iya kak, masih ada kok 😊", "14:31", isUser = false),
            MessageData(3, "Boleh kirim link pembayarannya kak? Mau langsung saya checkout", "14:32", isUser = true),
            MessageData(4, "semart.app/p/jeans-jacket-123", "14:33", isUser = false, type = ChatRowType.ACTIVE_LINK),
            MessageData(5, "Sesi transaksi ini telah berakhir", "Kemarin", isUser = false, type = ChatRowType.EXPIRED_LINK),
            // ✨ Update Skenario Baru
            MessageData(6, "Kak, minta link-nya lagi dong, yang barusan kedaluwarsa", "10:00", isUser = true),
            MessageData(7, "semart.app/p/jeans-jacket-456", "10:01", isUser = false, type = ChatRowType.ACTIVE_LINK)
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = Color.White,
                modifier = Modifier.border(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(end = 16.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = DarkText)
                    }

                    Image(
                        painter = painterResource(id = R.drawable.login_illustration),
                        contentDescription = "Foto Produk",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Andi Pratama (Penjual)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Jaket Denim Pria",
                            fontSize = 11.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
                    .border(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 46.dp),
                        placeholder = { Text("Tulis pesan ke penjual...", fontSize = 13.sp, color = GrayText) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkText),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedBorderColor = PrimaryBlue.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = false,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                conversation.add(
                                    MessageData(
                                        id = conversation.size + 1,
                                        text = messageText,
                                        time = currentTime,
                                        isUser = true
                                    )
                                )
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (messageText.isNotBlank()) PrimaryBlue else Color(0xFFE2E8F0))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Kirim",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(conversation) { message ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // SELLER AVATAR
                    if (!message.isUser) {
                        Image(
                            painter = painterResource(id = R.drawable.login_illustration),
                            contentDescription = "Avatar Penjual",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Box(
                        modifier = Modifier.weight(1f, fill = false),
                        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        when (message.type) {
                            ChatRowType.TEXT_MESSAGE -> {
                                ChatBubbleContent(text = message.text, time = message.time, isUser = message.isUser)
                            }
                            ChatRowType.ACTIVE_LINK -> {
                                SystemLinkMessageContent(isExpired = false, urlText = message.text, isUser = message.isUser, onNavigate = { onNavigateToCheckout() })
                            }
                            ChatRowType.EXPIRED_LINK -> {
                                SystemLinkMessageContent(isExpired = true, urlText = message.text, isUser = message.isUser)
                            }
                        }
                    }

                    // BUYER AVATAR
                    if (message.isUser) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("K", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// SUB-KOMPONEN KONTEN CHAT
// ==========================================

@Composable
fun ChatBubbleContent(text: String, time: String, isUser: Boolean) {
    Surface(
        color = if (isUser) PrimaryBlue else Color.White,
        shape = getBubbleShape(isUser),
        shadowElevation = 0.5.dp,
        modifier = Modifier.widthIn(max = 260.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
            Text(
                text = text,
                color = if (isUser) Color.White else DarkText,
                fontSize = 13.5.sp,
                lineHeight = 19.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = time,
                fontSize = 9.5.sp,
                color = if (isUser) Color.White.copy(alpha = 0.75f) else GrayText,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun SystemLinkMessageContent(isExpired: Boolean, urlText: String, isUser: Boolean, onNavigate: () -> Unit = {}) {
    val cardBg = if (isExpired) Color(0xFFFEE2E2) else SoftBlueBg
    val strokeColor = if (isExpired) Color(0xFFFCA5A5) else Color(0xFFBFDBFE)
    val titleColor = if (isExpired) Color(0xFFDC2626) else PrimaryBlue

    Surface(
        shape = getBubbleShape(isUser),
        color = cardBg,
        modifier = Modifier
            .widthIn(max = 260.dp)
            .border(1.dp, strokeColor, getBubbleShape(isUser))
            .clickable(
                enabled = !isExpired,
                onClick = { onNavigate() }
            )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isExpired) "Tautan Kedaluwarsa" else "Tautan Pembelian Resmi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = urlText,
                    fontSize = 12.sp,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = if (isExpired) Icons.Filled.Clear else Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = titleColor.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}