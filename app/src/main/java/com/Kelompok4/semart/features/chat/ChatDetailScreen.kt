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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.Kelompok4.semart.ui.theme.*
import com.Kelompok4.semart.features.chat.ChatViewModel
import com.Kelompok4.semart.features.chat.ChatState

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
    val productPrice: String? = null,
    val dateHeader: String = "",
    val senderName: String = "",
    val purchaseToken: String? = null
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
    viewModel: ChatViewModel = viewModel(),
    chatId: Int = 0,
    onBackClick: () -> Unit = {},
    onNavigateToCheckout: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        viewModel.loadChatSession(chatId)
    }

    if (state is ChatState.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    if (state is ChatState.Error) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text((state as ChatState.Error).message, color = Color.Red)
        }
        return
    }

    val session = (state as? ChatState.ChatDetailSuccess)?.session ?: return
    val chat = session.chat
    val currentPov = session.currentPov
    val messages = session.messages
    
    val currentUserId = if (currentPov == "buyer") chat.buyer.id else chat.seller.id
    val targetName = if (currentPov == "buyer") chat.seller.name else chat.buyer.name

    val conversation = remember(messages) {
        messages.map { msg ->
            val isUser = msg.senderId == currentUserId
            val type = if (msg.isLink) {
                if ( msg.purchaseLink?.valid == true) ChatRowType.ACTIVE_LINK else ChatRowType.EXPIRED_LINK
            } else {
                ChatRowType.TEXT_MESSAGE
            }
            
            var formattedTime = msg.createdAt ?: ""
            var dateHeader = ""
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = sdf.parse(msg.createdAt ?: "")
                if (date != null) {
                    val timeSdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    formattedTime = timeSdf.format(date)
                    
                    val dateSdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                    dateHeader = dateSdf.format(date)
                }
            } catch (e: Exception) {
                dateHeader = msg.createdAt?.substringBefore(" ") ?: ""
                formattedTime = msg.createdAt?.substringAfter(" ")?.take(5) ?: ""
            }

            MessageData(
                id = msg.id,
                text = msg.message ?: msg.purchaseLink?.checkoutUrl ?: "",
                time = formattedTime,
                isUser = isUser,
                type = type,
                productName = chat.product.name,
                productPrice = msg.purchaseLink?.priceLabel ?: "",
                dateHeader = dateHeader,
                senderName = targetName,
                purchaseToken = msg.purchaseLink?.token
            )
        }.toMutableList()
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

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SoftBlueBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = targetName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = targetName,
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
                                text = chat.product.name,
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
                                viewModel.sendMessage(chatId, messageText)
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
            val groupedMessages = conversation.groupBy { it.dateHeader }
            groupedMessages.forEach { (date, msgs) ->
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = date,
                                fontSize = 11.sp,
                                color = GrayText,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                items(msgs) { message ->
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
                                imageUrl = chat.product.imageUrl,
                                onCheckout = { 
                                    message.purchaseToken?.let { token ->
                                        onNavigateToCheckout(token)
                                    }
                                }
                            )
                        }
                        ChatRowType.EXPIRED_LINK -> {
                            PurchaseLinkCard(
                                isExpired = true,
                                productName = message.productName ?: "",
                                productPrice = message.productPrice ?: "",
                                time = message.time,
                                imageUrl = chat.product.imageUrl,
                                onCheckout = {}
                            )
                        }
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
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(SoftBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.senderName.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    fontSize = 10.sp
                )
            }
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
    imageUrl: String,
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
                .width(260.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                // ── Bagian atas: gambar + info produk ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Thumbnail produk
                    if (imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Gambar Produk",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.login_illustration),
                            contentDescription = "Gambar Produk",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // Nama produk
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

                        Spacer(modifier = Modifier.height(5.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isExpired) Icons.Filled.Info else Icons.Filled.LocalOffer,
                                contentDescription = null,
                                tint = if (isExpired) GrayText else PrimaryBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isExpired) "Link Kadaluwarsa" else "Sesuai Kesepakatan",
                                fontSize = 11.sp,
                                color = if (isExpired) GrayText else PrimaryBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── Tombol bawah ──
                Button(
                    onClick = { if (!isExpired) onCheckout() },
                    enabled = !isExpired,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFF1F5F9),
                        disabledContentColor = GrayText
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (isExpired) 0.dp else 2.dp
                    )
                ) {
                    Text(
                        text = if (isExpired) "Sesi Berakhir" else "Bayar Sekarang",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
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