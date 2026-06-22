package com.Kelompok4.semart.features.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.Kelompok4.semart.R
import com.Kelompok4.semart.data.model.Transaction
import com.Kelompok4.semart.features.transaction.TransactionViewModel
import com.Kelompok4.semart.features.transaction.TransactionState
import com.Kelompok4.semart.ui.theme.*

// ── Data Model ──
enum class TransactionStatus(val displayName: String) {
    BAYAR("Menunggu Pembayaran"),
    MENUNGGU("Menunggu Konfirmasi"),
    SELESAI("Selesai"),
    GAGAL("Gagal")
}

private fun mapStatus(apiStatus: String): TransactionStatus {
    return when (apiStatus) {
        "menunggu_pembayaran" -> TransactionStatus.BAYAR
        "dibayar" -> TransactionStatus.MENUNGGU
        "selesai" -> TransactionStatus.SELESAI
        "gagal" -> TransactionStatus.GAGAL
        else -> TransactionStatus.BAYAR
    }
}

private fun formatPaymentMethod(method: String?): String {
    if (method == null) return "-"
    return when (method.lowercase()) {
        "transfer_bca", "bca" -> "BCA"
        "dana" -> "Dana"
        "shopeepay" -> "ShopeePay"
        "ovo" -> "OVO"
        "gopay" -> "GoPay"
        "linkaja" -> "LinkAja"
        else -> method.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    viewModel: TransactionViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPurchaseHistory()
    }

    val allTransactions = if (state is TransactionState.Success) {
        (state as TransactionState.Success).transactions
    } else emptyList()

    val filters = listOf("Semua", "Bayar", "Menunggu", "Selesai", "Gagal")
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var transactionForReview by remember { mutableStateOf<Transaction?>(null) }
    var transactionForViewReview by remember { mutableStateOf<Transaction?>(null) }

    val displayedTransactions = if (selectedFilter == "Semua") {
        allTransactions
    } else {
        allTransactions.filter {
            when (selectedFilter) {
                "Bayar" -> mapStatus(it.status) == TransactionStatus.BAYAR
                "Menunggu" -> mapStatus(it.status) == TransactionStatus.MENUNGGU
                "Selesai" -> mapStatus(it.status) == TransactionStatus.SELESAI
                "Gagal" -> mapStatus(it.status) == TransactionStatus.GAGAL
                else -> true
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 0.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Panah kembali
                        IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = DarkText)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Riwayat Transaksi",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "Daftar transaksi pembelian Anda",
                                fontSize = 12.sp,
                                color = GrayText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Filter Chips
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filters) { filter ->
                            FilterChipItem(
                                text = filter,
                                isSelected = selectedFilter == filter,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = BorderGray, thickness = 1.dp)
                }
            }
        }
    ) { innerPadding ->
        when (state) {
            is TransactionState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is TransactionState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text((state as TransactionState.Error).message, color = Color.Red, fontSize = 13.sp)
                }
            }
            else -> {
                if (displayedTransactions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Text("Tidak ada transaksi untuk filter ini.", color = GrayText, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayedTransactions, key = { it.id }) { transaction ->
                            TransactionCard(
                                transaction = transaction,
                                onClick = { selectedTransaction = transaction },
                                onReviewClick = { transactionForReview = transaction },
                                onViewReviewClick = { transactionForViewReview = transaction }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Detail Pesanan
    selectedTransaction?.let { transaction ->
        TransactionDetailModal(
            transaction = transaction,
            onDismiss = { selectedTransaction = null }
        )
    }

    // Modal Submit Review
    transactionForReview?.let { transaction ->
        ReviewSubmissionModal(
            transaction = transaction,
            onDismiss = { transactionForReview = null },
            onSubmit = { rating, comment ->
                viewModel.postReview(transaction.id, rating, comment)
                transactionForReview = null
            }
        )
    }

    // Modal View Review
    transactionForViewReview?.let { transaction ->
        ViewReviewModal(
            transaction = transaction,
            onDismiss = { transactionForViewReview = null }
        )
    }
}

// ── Komponen Card Transaksi (Sesuai Desain UI) ──
@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit,
    onReviewClick: () -> Unit,
    onViewReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            // Gambar Produk
            if (transaction.product.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = transaction.product.imageUrl,
                    contentDescription = transaction.product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SoftBlueBg)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.login_illustration),
                    contentDescription = transaction.product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SoftBlueBg)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Detail Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "oleh ${transaction.seller.name ?: "-"}",
                    fontSize = 11.sp,
                    color = GrayText
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val isSelesai = mapStatus(transaction.status) == TransactionStatus.SELESAI
                
                if (isSelesai) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusChip(status = TransactionStatus.SELESAI)
                        Text(text = transaction.priceLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, fontFamily = Poppins)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = transaction.dateLabel ?: transaction.date ?: "-", fontSize = 11.sp, color = GrayText, fontFamily = Poppins)
                        if (transaction.review == null) {
                            Button(
                                onClick = onReviewClick,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Ulasan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                            }
                        } else {
                            OutlinedButton(
                                onClick = onViewReviewClick,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                                border = BorderStroke(1.dp, PrimaryBlue),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Lihat Ulasan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = Poppins)
                            }
                        }
                    }
                } else {
                    StatusChip(status = mapStatus(transaction.status))
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = transaction.dateLabel ?: transaction.date ?: "-", fontSize = 11.sp, color = GrayText, fontFamily = Poppins)
                        Text(text = transaction.priceLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue, fontFamily = Poppins)
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Panah Kanan
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Detail",
                tint = GrayText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── Chip Filter Kategori ──
@Composable
fun FilterChipItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryBlue else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryBlue else BorderGray,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) Color.White else DarkText
        )
    }
}

// ── Chip Status (Menunggu, Selesai, Gagal) ──
@Composable
fun StatusChip(status: TransactionStatus) {
    val (bgColor, contentColor, icon) = when (status) {
        TransactionStatus.BAYAR -> Triple(NetralBg, NetralText, Icons.Outlined.AccessTime)
        TransactionStatus.MENUNGGU -> Triple(WarnBg, WarnText, Icons.Outlined.AccessTime)
        TransactionStatus.SELESAI -> Triple(SuccessBg, SuccessText, Icons.Filled.CheckCircle)
        TransactionStatus.GAGAL -> Triple(DangerBg, DangerText, Icons.Outlined.Cancel)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = status.displayName, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = contentColor)
    }
}

// ── Modal Detail Pesanan (Mengadopsi UI Checkout) ──
@Composable
fun TransactionDetailModal(
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    val uiStatus = mapStatus(transaction.status)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // Header Modal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Rincian Pesanan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = GrayText, modifier = Modifier.size(20.dp))
                    }
                }

                Divider(color = BorderGray, modifier = Modifier.padding(vertical = 12.dp))

                // Produk Info
                Row(verticalAlignment = Alignment.Top) {
                    if (transaction.product.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = transaction.product.imageUrl,
                            contentDescription = "Foto Produk",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SoftBlueBg)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.login_illustration),
                            contentDescription = "Foto Produk",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SoftBlueBg)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            transaction.product.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkText,
                            lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            transaction.priceLabel,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }

                Divider(color = BorderGray, modifier = Modifier.padding(vertical = 14.dp))

                // Detail Meta
                MetaRow(label = "Status", value = transaction.statusLabel,
                    valueColor = when(uiStatus) {
                        TransactionStatus.BAYAR -> WarnText
                        TransactionStatus.MENUNGGU -> WarnText
                        TransactionStatus.SELESAI -> SuccessText
                        TransactionStatus.GAGAL -> DangerText
                    }
                )
                Spacer(Modifier.height(8.dp))
                MetaRow(label = "Tanggal Transaksi", value = transaction.dateLabel ?: transaction.date ?: "-", valueColor = DarkText)
                Spacer(Modifier.height(8.dp))
                MetaRow(label = "Seller", value = transaction.seller.name ?: "-", valueColor = PrimaryBlue)
                if (transaction.paymentMethod != null) {
                    Spacer(Modifier.height(8.dp))
                    MetaRow(label = "Metode Pembayaran", value = formatPaymentMethod(transaction.paymentMethod), valueColor = DarkText)
                }

                Divider(color = BorderGray, modifier = Modifier.padding(vertical = 14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Pembayaran", fontSize = 13.sp, color = GrayText)
                    Text(transaction.priceLabel, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                }

                Spacer(Modifier.height(16.dp))

                // Alert Info berdasarkan status
                when (uiStatus) {
                    TransactionStatus.BAYAR -> {
                        InfoAlertCard(
                            bg = NetralBg, border = NetralBorder, iconColor = NetralText, icon = Icons.Outlined.AccessTime,
                            title = "Menunggu Pembayaran",
                            body = "Silakan lakukan pembayaran dan unggah bukti pembayaran."
                        )
                    }
                    TransactionStatus.MENUNGGU -> {
                        InfoAlertCard(
                            bg = WarnBg, border = WarnBorder, iconColor = WarnText, icon = Icons.Outlined.AccessTime,
                            title = "Menunggu Verifikasi",
                            body = "Pembayaranmu sedang diperiksa oleh penjual. Harap bersabar."
                        )
                    }
                    TransactionStatus.SELESAI -> {
                        InfoAlertCard(
                            bg = SuccessBg, border = SuccessBorder, iconColor = SuccessIcon, icon = Icons.Filled.CheckCircle,
                            title = "Transaksi Berhasil",
                            body = "Barang telah diterima dan transaksi telah diselesaikan. Jangan lupa beri ulasan!"
                        )
                    }
                    TransactionStatus.GAGAL -> {
                        InfoAlertCard(
                            bg = DangerBg, border = DangerBorder, iconColor = DangerIcon, icon = Icons.Filled.Cancel,
                            title = "Transaksi Dibatalkan",
                            body = "Transaksi ini dibatalkan karena pembayaran tidak valid atau melewati batas waktu."
                        )
                    }
                }
            }
        }
    }
}

// ── Reusable Component untuk Modal Meta Info ──
@Composable
private fun MetaRow(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = GrayText)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

// ── Reusable Component Alert Info dari Checkout ──
@Composable
private fun InfoAlertCard(bg: Color, border: Color, iconColor: Color, icon: ImageVector, title: String, body: String) {
    Surface(color = bg, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, border), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = iconColor)
                Text(body, fontSize = 11.sp, color = iconColor.copy(alpha = 0.8f), lineHeight = 15.sp, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

// ── Modals untuk Ulasan ──
@Composable
fun ReviewSubmissionModal(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Berikan Ulasan Produk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = GrayText, modifier = Modifier.size(20.dp))
                    }
                }

                Divider(color = BorderGray, modifier = Modifier.padding(vertical = 12.dp))

                // Produk Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (transaction.product.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = transaction.product.imageUrl,
                            contentDescription = "Foto Produk",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(SoftBlueBg)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.login_illustration),
                            contentDescription = "Foto Produk",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(SoftBlueBg)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(transaction.product.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Text(transaction.seller.name ?: "-", fontSize = 11.sp, color = GrayText)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Kualitas Produk & Pelayanan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (i <= rating) Color(0xFFFFD700) else GrayText,
                            modifier = Modifier.size(32.dp).clickable { rating = i }
                        )
                    }
                }
                if (rating == 0) {
                    Text("Pilih bintang untuk memberi nilai", fontSize = 11.sp, color = GrayText, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(Modifier.height(16.dp))

                Text("Tulis Ulasan Anda", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Bagikan pengalaman Anda saat berbelanja", fontSize = 12.sp, color = GrayText) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, fontFamily = Poppins, color = DarkText),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = BorderGray
                    )
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { onSubmit(rating, comment) },
                    enabled = rating > 0,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Kirim Ulasan", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
                }
            }
        }
    }
}

@Composable
fun ViewReviewModal(
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ulasan Anda", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = GrayText, modifier = Modifier.size(20.dp))
                    }
                }

                Divider(color = BorderGray, modifier = Modifier.padding(vertical = 12.dp))

                // Produk Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (transaction.product.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = transaction.product.imageUrl,
                            contentDescription = "Foto Produk",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(SoftBlueBg)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.login_illustration),
                            contentDescription = "Foto Produk",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(SoftBlueBg)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(transaction.product.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Text(transaction.seller.name ?: "-", fontSize = 11.sp, color = GrayText)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Kualitas Produk & Pelayanan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val rating = transaction.review?.rating ?: 0
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (i <= rating) Color(0xFFFFD700) else GrayText,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("Ulasan Anda", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = transaction.review?.comment.takeIf { !it.isNullOrBlank() } ?: "Tidak ada komentar",
                        fontSize = 13.sp,
                        color = DarkText
                    )
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Tutup", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins)
                }
            }
        }
    }
}