package com.Kelompok4.semart.features.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
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
import com.Kelompok4.semart.R

// Konstanta Warna
val PrimaryBlue = Color(0xFF3B9DF8)
val DarkText = Color(0xFF243447)
val GrayText = Color(0xFF6B7280)
val BackgroundGray = Color(0xFFF8FAFC)
val CardBorder = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()
    var selectedPayment by remember { mutableStateOf("BCA") }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Checkout & Pembayaran", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = DarkText) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. RINGKASAN PESANAN
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = borderStroke(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ringkasan Pesanan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkText)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Detail Produk
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.login_illustration),
                            contentDescription = "Foto Produk",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF1F5F9))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Laptop MacBook Air M1 2020", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Rp 7.500.000", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Qty: 1", fontSize = 12.sp, color = GrayText)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CardBorder)

                    // Subtotal & Ongkir
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 13.sp, color = GrayText)
                        Text("Rp 7.500.000", fontSize = 13.sp, color = DarkText)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Ongkos Kirim", fontSize = 13.sp, color = GrayText)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Rp 0", fontSize = 13.sp, color = GrayText)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                Text("Gratis", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CardBorder)

                    // Total Pembayaran
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Total Pembayaran", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Text("Rp 7.500.000", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = CardBorder)

                    // Timer Berlaku Hingga
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Schedule, contentDescription = null, tint = GrayText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Berlaku Hingga", fontSize = 13.sp, color = GrayText)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TimerBox("23")
                            Text(" : ", color = Color(0xFFEA580C), fontWeight = FontWeight.Bold)
                            TimerBox("59")
                            Text(" : ", color = Color(0xFFEA580C), fontWeight = FontWeight.Bold)
                            TimerBox("45")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = GrayText, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 2. PILIH METODE PEMBAYARAN
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = borderStroke(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pilih Metode Pembayaran", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkText)
                    Spacer(modifier = Modifier.height(12.dp))

                    PaymentOptionItem(
                        title = "BCA",
                        subtitle = "1234567890\na/n Andi Pratama",
                        isSelected = selectedPayment == "BCA",
                        onClick = { selectedPayment = "BCA" }
                    )
                    PaymentOptionItem(
                        title = "ShopeePay",
                        subtitle = "081234567890",
                        isSelected = selectedPayment == "ShopeePay",
                        onClick = { selectedPayment = "ShopeePay" }
                    )
                    PaymentOptionItem(
                        title = "gopay",
                        subtitle = "081234567890",
                        isSelected = selectedPayment == "Gopay",
                        onClick = { selectedPayment = "Gopay" }
                    )

                    // Info Box
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 16.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Metode pembayaran ditentukan oleh seller.", fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.Medium)
                                Text("Pastikan kamu memilih metode yang tersedia.", fontSize = 11.sp, color = GrayText)
                            }
                        }
                    }

                    // Tombol Bayar
                    Button(
                        onClick = { /* Aksi Bayar */ },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bayar Sekarang", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // 3. STATUS BANNERS
            StatusBanner(
                icon = Icons.Filled.Schedule,
                iconColor = Color(0xFFEA580C),
                bgColor = Color(0xFFFFF7ED),
                borderColor = Color(0xFFFFEDD5),
                title = "Selesaikan pembayaran sebelum waktu habis.",
                subtitle = "Sisa waktu:  23 : 59 : 45"
            )
            StatusBanner(
                icon = Icons.Filled.Warning,
                iconColor = Color(0xFFDC2626),
                bgColor = Color(0xFFFEF2F2),
                borderColor = Color(0xFFFEE2E2),
                title = "Pesanan dibatalkan jika pembayaran gagal.",
                subtitle = "Pastikan nominal sesuai."
            )
            StatusBanner(
                icon = Icons.Outlined.CheckCircle,
                iconColor = Color(0xFF16A34A),
                bgColor = Color(0xFFF0FDF4),
                borderColor = Color(0xFFDCFCE7),
                title = "Menunggu konfirmasi dari seller setelah pembayaran berhasil.",
                subtitle = null
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Komponen Pembantu
@Composable
fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)

@Composable
fun TimerBox(time: String) {
    Surface(
        color = Color(0xFFFFF7ED),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5))
    ) {
        Text(time, color = Color(0xFFEA580C), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
    }
}

@Composable
fun PaymentOptionItem(title: String, subtitle: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PrimaryBlue else CardBorder),
        color = if (isSelected) Color(0xFFF0F7FF) else Color.White,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Placeholder Logo/Icon Pembayaran
            Box(modifier = Modifier.size(32.dp).background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                Text(title.take(1), fontWeight = FontWeight.Bold, color = GrayText)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkText)
                Text(subtitle, fontSize = 12.sp, color = GrayText, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
fun StatusBanner(icon: ImageVector, iconColor: Color, bgColor: Color, borderColor: Color, title: String, subtitle: String?) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 12.sp, color = iconColor, fontWeight = FontWeight.SemiBold)
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(subtitle, fontSize = 12.sp, color = DarkText)
                }
            }
        }
    }
}