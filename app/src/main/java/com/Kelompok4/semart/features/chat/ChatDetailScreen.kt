package com.Kelompok4.semart.features.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
    val type: ChatRowType = ChatRowType.TEXT_MESSAGE,
    val productName: String? = null,
    val productPrice: String? = null
)

fun getBubbleShape(isUser: Boolean): RoundedCornerShape {
    return if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }
}

// ==========================================
// CHAT DETAIL SCREEN
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    onBackClick: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val conversation = remember {
        mutableStateListOf(
            MessageData(1, "Halo, apakah Jaket Denim Pria nya masih ada kak?", "14:30", isUser = true),
            MessageData(2, "Iya kak, masih ada kok 😊", "14:31", isUser = false),
            MessageData(3, "Boleh kirim link pembayarannya kak? Mau langsung saya checkout", "14:32", isUser = true),
            MessageData(
                id = 4,
                text = "semart.app/p/jeans-jacket-123",
                time = "14:33",
                isUser = false,
                type = ChatRowType.ACTIVE_LINK,
                productName = "Laptop Lenovo Thinkpad Bekas",
                productPrice = "Rp 3.500.000"
            ),
            MessageData(
                id = 5,
                text = "Sesi transaksi ini telah berakhir",
                time = "Kemarin",
                isUser = false,
                type = ChatRowType.EXPIRED_LINK,
                productName = "Laptop Lenovo Thinkpad Bekas",
                productPrice = "Rp 3.500.000"
            ),
            MessageData(6, "Kak, minta link-nya lagi dong, yang barusan kedaluwarsa", "10:00", isUser = true),
            MessageData(
                id = 7,
                text = "semart.app/p/jeans-jacket-456",
                time = "10:01",
                isUser = false,
                type = ChatRowType.ACTIVE_LINK,
                productName = "Laptop Lenovo Thinkpad Bekas",
                productPrice = "Rp 3.500.000"
            )
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(end = 16.dp, top = 6.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = DarkText
                        )
                    }

                    // Avatar penjual — pakai drawable login_illustration
                    Image(
                        painter = painterResource(id = R.drawable.login_illustration),
                        contentDescription = "Foto Penjual",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nurul Janati",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Store,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Laptop Lenovo Thinkpad Bekas",
                                fontSize = 11.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // POV Buyer: hanya input pesan, tanpa banner kirim link
            Surface(
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp),
                        placeholder = {
                            Text(
                                text = "Tulis pesan...",
                                fontSize = 13.sp,
                                color = GrayText
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 13.sp,
                            color = DarkText
                        ),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5F9),
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedBorderColor = PrimaryBlue.copy(alpha = 0.4f),
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = false,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val canSend = messageText.isNotBlank()
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (canSend) PrimaryBlue else Color(0xFFE2E8F0))
                            .clickable(enabled = canSend) {
                                val currentTime =
                                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                conversation.add(
                                    MessageData(
                                        id = conversation.size + 1,
                                        text = messageText.trim(),
                                        time = currentTime,
                                        isUser = true
                                    )
                                )
                                messageText = ""
                            },
                        contentAlignment = Alignment.Center
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
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(conversation) { message ->
                when (message.type) {
                    ChatRowType.TEXT_MESSAGE -> {
                        ChatBubbleRow(message = message)
                    }
                    ChatRowType.ACTIVE_LINK -> {
                        PurchaseLinkCard(
                            isExpired = false,
                            productName = message.productName ?: "",
                            productPrice = message.productPrice ?: "",
                            time = message.time,
                            onCheckout = onNavigateToCheckout
                        )
                    }
                    ChatRowType.EXPIRED_LINK -> {
                        PurchaseLinkCard(
                            isExpired = true,
                            productName = message.productName ?: "",
                            productPrice = message.productPrice ?: "",
                            time = message.time,
                            onCheckout = {}
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// CHAT BUBBLE ROW
// ==========================================

@Composable
fun ChatBubbleRow(message: MessageData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            // Avatar penjual
            Image(
                painter = painterResource(id = R.drawable.login_illustration),
                contentDescription = "Avatar Penjual",
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(max = 250.dp),
            contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            ChatBubbleContent(
                text = message.text,
                time = message.time,
                isUser = message.isUser
            )
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // Avatar buyer
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Avatar Buyer",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==========================================
// CHAT BUBBLE CONTENT
// ==========================================

@Composable
fun ChatBubbleContent(text: String, time: String, isUser: Boolean) {
    Surface(
        color = if (isUser) PrimaryBlue else Color.White,
        shape = getBubbleShape(isUser),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                text = text,
                color = if (isUser) Color.White else DarkText,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = time,
                fontSize = 10.sp,
                color = if (isUser) Color.White.copy(alpha = 0.7f) else GrayText,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// ==========================================
// PURCHASE LINK CARD — tampil di tengah
// ==========================================

@Composable
fun PurchaseLinkCard(
    isExpired: Boolean,
    productName: String,
    productPrice: String,
    time: String,
    onCheckout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column {
                // ── Bagian atas: gambar + info produk ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Thumbnail produk
                    Image(
                        painter = painterResource(id = R.drawable.login_illustration),
                        contentDescription = "Gambar Produk",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // Nama produk — coret jika expired
                        Text(
                            text = productName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isExpired) GrayText else DarkText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (isExpired) TextDecoration.LineThrough else TextDecoration.None
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Harga
                        Text(
                            text = productPrice,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpired) GrayText else PrimaryBlue
                        )

                        if (isExpired) {
                            Spacer(modifier = Modifier.height(5.dp))
                            // Label "Link Kadaluwarsa" — pakai biru abu, bukan merah
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = GrayText,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Link Kadaluwarsa",
                                    fontSize = 11.sp,
                                    color = GrayText,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Divider
                Divider(
                    color = Color(0xFFE2E8F0),
                    thickness = 1.dp
                )

                // ── Tombol bawah ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isExpired) Color(0xFFF1F5F9) else Color.White,
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp,
                                bottomEnd = 16.dp
                            )
                        )
                        .clickable(enabled = !isExpired) { onCheckout() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isExpired) {
                        Text(
                            text = "Sesi Berakhir",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GrayText
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Lanjut ke Pembayaran",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Timestamp di bawah card
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = time,
            fontSize = 10.sp,
            color = GrayText
        )
    }
}