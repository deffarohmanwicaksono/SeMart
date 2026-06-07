package com.Kelompok4.semart.features.checkout

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.Kelompok4.semart.R
import kotlinx.coroutines.delay

// ─── Warna ───────────────────────────────────────────────────────────────────
private val PrimaryBlue    = Color(0xFF3B9DF8)
private val BlueLight      = Color(0xFFEBF5FF)
private val DarkText       = Color(0xFF1E293B)
private val MedText        = Color(0xFF475569)
private val GrayText       = Color(0xFF94A3B8)
private val BgPage         = Color(0xFFF1F5F9)
private val CardBg         = Color(0xFFFFFFFF)
private val CardBorder     = Color(0xFFE2E8F0)
private val WarnBg         = Color(0xFFFFFBEB)
private val WarnBorder     = Color(0xFFFCD34D)
private val WarnText       = Color(0xFF92400E)
private val DangerBg       = Color(0xFFFEF2F2)
private val DangerBorder   = Color(0xFFFCA5A5)
private val DangerText     = Color(0xFF991B1B)
private val SuccessBg      = Color(0xFFF0FDF4)
private val SuccessBorder  = Color(0xFF86EFAC)
private val SuccessIcon    = Color(0xFF22C55E)
private val DividerColor   = Color(0xFFF1F5F9)

// ─── Main Screen ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onBackClick: () -> Unit = {}) {

    // ── state ──────────────────────────────────────────────────────────────
    var selectedPayment  by remember { mutableStateOf("transfer_bca") }
    var showModal        by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }

    // ── countdown ──────────────────────────────────────────────────────────
    var totalSeconds by remember { mutableStateOf(23 * 3600 + 47 * 60 + 0L) }
    LaunchedEffect(Unit) {
        while (totalSeconds > 0) {
            delay(1_000)
            totalSeconds--
        }
    }
    val cdH = totalSeconds / 3600
    val cdM = (totalSeconds % 3600) / 60
    val cdS = totalSeconds % 60

    // ── metode pembayaran ──────────────────────────────────────────────────
    val paymentMethods = listOf(
        PaymentMethod("transfer_bca",      "Transfer Bank",  "BCA",     Icons.Outlined.AccountBalance),
        PaymentMethod("transfer_mandiri",  "Transfer Bank",  "Mandiri", Icons.Outlined.AccountBalance),
        PaymentMethod("gopay",             "E-Wallet",       "GoPay",   Icons.Outlined.PhoneAndroid),
        PaymentMethod("ovo",               "E-Wallet",       "OVO",     Icons.Outlined.PhoneAndroid),
    )

    Scaffold(
        containerColor = BgPage,
        topBar = { CheckoutTopBar(onBackClick) }
    ) { innerPad ->

        Column(
            modifier = Modifier
                .padding(innerPad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 1 ─ Ringkasan pesanan
            OrderSummaryCard()

            // 2 ─ Pilih metode pembayaran & Tombol Bayar (Sekarang di atas alert)
            PaymentMethodCard(
                methods        = paymentMethods,
                selected       = selectedPayment,
                onSelect       = { selectedPayment = it },
                onPayClick     = { showModal = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3 ─ Alert Info (Sekarang di bawah)
            CountdownAlert(cdH, cdM, cdS)

            InfoAlertCard(
                bg        = DangerBg,
                border    = DangerBorder,
                iconColor = Color(0xFFEF4444),
                icon      = Icons.Filled.Warning,
                title     = "Pesanan akan dibatalkan",
                body      = "jika pembayaran gagal atau nominal tidak sesuai. Pastikan bukti pembayaran sesuai dengan nominal yang tertera."
            )

            InfoAlertCard(
                bg        = SuccessBg,
                border    = SuccessBorder,
                iconColor = SuccessIcon,
                icon      = Icons.Filled.CheckCircle,
                title     = "Setelah pembayaran berhasil",
                body      = "seller akan menerima notifikasi dan segera memproses pesanan Anda. Status pesanan dapat dilihat di menu Riwayat Pembelian."
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showModal) {
        UploadProofModal(
            selectedMethod = paymentMethods.first { it.id == selectedPayment },
            onDismiss      = { showModal = false },
            onConfirm      = {
                showModal        = false
                showSuccessModal = true
            }
        )
    }

    if (showSuccessModal) {
        PaymentSuccessModal(onDone = { showSuccessModal = false })
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────
@Composable
private fun CheckoutTopBar(onBackClick: () -> Unit) {
    Surface(color = CardBg, shadowElevation = 1.dp) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali", tint = DarkText)
            }
            Text(
                "Checkout",
                fontSize   = 17.sp,
                fontWeight = FontWeight.Bold,
                color      = DarkText
            )
        }
    }
}

// ─── Order Summary Card ───────────────────────────────────────────────────────
@Composable
private fun OrderSummaryCard() {
    SectionCard {
        Text("Ringkasan Pesanan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
        Spacer(Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.Top) {
            // Gambar disamakan dengan dummy
            Image(
                painter = painterResource(id = R.drawable.login_illustration),
                contentDescription = "Foto Produk",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BlueLight)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Laptop MacBook Air M1 2020",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = DarkText,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Rp 7.500.000",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PrimaryBlue
                )
                Spacer(Modifier.height(6.dp))
                ConditionPill(label = "Bekas Baik")
            }
        }

        Divider(color = DividerColor, modifier = Modifier.padding(vertical = 14.dp))

        MetaRow(label = "Seller", value = "Ahmad Fauzan", valueColor = PrimaryBlue)
        Spacer(Modifier.height(6.dp))

        // Warna batas pembayaran diubah jadi DarkText
        MetaRow(label = "Batas Pembayaran", value = "08 Jun 2026 • 23:47 WIB", valueColor = DarkText)

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(BlueLight).padding(10.dp)
        ) {
            Icon(Icons.Outlined.Chat, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp).padding(top = 1.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                "Harga telah disepakati. Silakan lakukan pembayaran sebelum batas waktu berakhir.",
                fontSize = 11.sp, color = MedText, lineHeight = 15.sp, modifier = Modifier.weight(1f)
            )
        }

        Divider(color = DividerColor, modifier = Modifier.padding(vertical = 14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("Total Pembayaran", fontSize = 13.sp, color = MedText)
            Text("Rp 7.500.000", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
        }
    }
}

// ─── Payment Method Card ──────────────────────────────────────────────────────
@Composable
private fun PaymentMethodCard(
    methods    : List<PaymentMethod>,
    selected   : String,
    onSelect   : (String) -> Unit,
    onPayClick : () -> Unit
) {
    SectionCard {
        Text("Pilih Metode Pembayaran", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
        Spacer(Modifier.height(12.dp))

        methods.forEach { method ->
            val isSelected = method.id == selected
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.5.dp, if (isSelected) PrimaryBlue else CardBorder, RoundedCornerShape(10.dp))
                    .background(if (isSelected) BlueLight else CardBg)
                    .clickable { onSelect(method.id) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(if (isSelected) PrimaryBlue else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(method.icon, contentDescription = null, tint = if (isSelected) Color.White else GrayText, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(method.type, fontSize = 10.sp, color = GrayText)
                    Text(method.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) PrimaryBlue else DarkText)
                }
                if (isSelected) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(BlueLight).padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Outlined.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                "Metode pembayaran ditentukan oleh seller. Pilih metode yang tersedia agar pesananmu diproses dengan cepat.",
                fontSize = 11.sp, color = MedText, lineHeight = 15.sp, modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick   = onPayClick,
            modifier  = Modifier.fillMaxWidth().height(50.dp),
            shape     = RoundedCornerShape(10.dp),
            colors    = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("Bayar Sekarang", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Upload Proof Modal ───────────────────────────────────────────────────────
@Composable
fun UploadProofModal(
    selectedMethod : PaymentMethod,
    onDismiss      : () -> Unit,
    onConfirm      : () -> Unit
) {
    // State untuk menyimpan URI dari gambar yang dipilih dari galeri
    var imageUri  by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    // Launcher untuk buka galeri HP bawaan
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = CardBg
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Upload Bukti Pembayaran", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = GrayText, modifier = Modifier.size(20.dp))
                    }
                }

                Divider(color = DividerColor, modifier = Modifier.padding(vertical = 12.dp))

                Text("Foto Bukti Pembayaran", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text("Screenshot atau foto struk transfer yang jelas", fontSize = 11.sp, color = GrayText, modifier = Modifier.padding(top = 2.dp, bottom = 10.dp))

                // Area Upload (Bisa Diklik Untuk Buka Galeri)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (imageUri != null) BlueLight else Color(0xFFF8FAFC))
                        .border(1.5.dp, if (imageUri != null) PrimaryBlue else CardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            // Trigger buka galeri (hanya gambar)
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Image, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(6.dp))
                            Text("Gambar Berhasil Dipilih!", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryBlue)
                            Spacer(Modifier.height(4.dp))
                            TextButton(onClick = { imageUri = null }) {
                                Icon(Icons.Filled.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Hapus", fontSize = 11.sp, color = Color(0xFFEF4444))
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(BlueLight), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(26.dp))
                            }
                            Spacer(Modifier.height(10.dp))
                            Text("Tap untuk pilih foto", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                            Text("Buka Galeri • Maks 5 MB", fontSize = 10.sp, color = GrayText, modifier = Modifier.padding(top = 3.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss, modifier = Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CardBorder), colors = ButtonDefaults.outlinedButtonColors(contentColor = MedText)
                    ) {
                        Text("Batal", fontWeight = FontWeight.Medium)
                    }

                    Button(
                        onClick = {
                            if (imageUri != null) {
                                uploading = true
                                onConfirm()
                            }
                        },
                        modifier = Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (imageUri != null) PrimaryBlue else GrayText),
                        enabled = imageUri != null
                    ) {
                        if (uploading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Kirim Bukti", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ─── Success Modal (Sama seperti sebelumnya) ──────────────────────────────────
@Composable
fun PaymentSuccessModal(onDone: () -> Unit) {
    Dialog(onDismissRequest = onDone, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), shape = RoundedCornerShape(20.dp), color = CardBg) {
            Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(SuccessBg), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessIcon, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text("Bukti Terkirim!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Spacer(Modifier.height(6.dp))
                Text("Bukti pembayaranmu sudah diterima. Seller akan segera memverifikasi dan memproses pesananmu.", fontSize = 12.sp, color = MedText, lineHeight = 17.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Alert & Utility Components (Sisa kode pembantu) ─────────────────────────
@Composable
private fun CountdownAlert(hours: Long, minutes: Long, seconds: Long) {
    Surface(color = WarnBg, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, WarnBorder), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = WarnText, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Pembayaran harus diselesaikan sebelum waktu habis.", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = WarnText)
                Text("Sisa waktu pembayaran: $hours Jam $minutes Menit $seconds Detik", fontSize = 11.sp, color = WarnText, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun InfoAlertCard(bg: Color, border: Color, iconColor: Color, icon: ImageVector, title: String, body: String) {
    Surface(color = bg, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, border), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(body, fontSize = 11.sp, color = MedText, lineHeight = 15.sp, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, CardBorder)) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun ConditionPill(label: String) {
    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(BlueLight).padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = PrimaryBlue)
    }
}

@Composable
private fun MetaRow(label: String, value: String, valueColor: Color = MedText) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = GrayText)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

data class PaymentMethod(val id: String, val type: String, val name: String, val icon: ImageVector)