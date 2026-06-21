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
    MENUNGGU("Menunggu Konfirmasi"),
    SELESAI("Selesai"),
    GAGAL("Gagal")
}

private fun mapStatus(apiStatus: String): TransactionStatus {
    return when (apiStatus) {
        "menunggu_pembayaran", "dibayar" -> TransactionStatus.MENUNGGU
        "selesai" -> TransactionStatus.SELESAI
        "gagal" -> TransactionStatus.GAGAL
        else -> TransactionStatus.MENUNGGU
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

    val filters = listOf("Semua", "Menunggu", "Selesai", "Gagal")
    var selectedFilter by remember { mutableStateOf(filters[0]) }

    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    val displayedTransactions = if (selectedFilter == "Semua") {
        allTransactions
    } else {
        allTransactions.filter {
            when (selectedFilter) {
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
                                onClick = { selectedTransaction = transaction }
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
}

// ── Komponen Card Transaksi (Sesuai Desain UI) ──
@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit
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
                StatusChip(status = mapStatus(transaction.status))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = transaction.dateLabel ?: transaction.date ?: "-", fontSize = 11.sp, color = GrayText)
                    Text(text = transaction.priceLabel, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
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