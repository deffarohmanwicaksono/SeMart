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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.Kelompok4.semart.R
import com.Kelompok4.semart.data.remote.CheckoutDetailResponse
import com.Kelompok4.semart.features.checkout.CheckoutViewModel
import com.Kelompok4.semart.features.checkout.CheckoutState
import kotlinx.coroutines.delay
import com.Kelompok4.semart.ui.theme.*

// ─── Main Screen ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = viewModel(),
    token: String = "",
    onBackClick: () -> Unit = {},
    onCheckoutSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // ── state ──────────────────────────────────────────────────────────────
    var selectedPayment  by remember { mutableStateOf("") }
    var showModal        by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }
    var transactionId    by remember { mutableStateOf(0) }
    var cachedDetail     by remember { mutableStateOf<CheckoutDetailResponse?>(null) }

    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadCheckoutDetail(token)
        }
    }

    LaunchedEffect(state) {
        if (state is CheckoutState.DetailSuccess) {
            val detail = (state as CheckoutState.DetailSuccess).detail
            cachedDetail = detail
            if (selectedPayment.isEmpty() && !detail.paymentAccounts.isNullOrEmpty()) {
                selectedPayment = detail.paymentAccounts.first().providerName
            }
        } else if (state is CheckoutState.ProcessCheckoutSuccess) {
            val response = (state as CheckoutState.ProcessCheckoutSuccess).response
            transactionId = response.transactionId
            showModal = true
        } else if (state is CheckoutState.UploadProofSuccess) {
            showModal = false
            showSuccessModal = true
        }
    }

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

    if (cachedDetail != null) {
        val detail = cachedDetail!!
        val paymentMethods = detail.paymentAccounts?.map { acc ->
            PaymentMethod(
                id = acc.providerName,
                type = acc.type,
                name = acc.providerName,
                icon = if (acc.type.contains("Bank", ignoreCase = true)) Icons.Outlined.AccountBalance else Icons.Outlined.PhoneAndroid,
                accountNumber = acc.accountNumber,
                accountName = acc.accountName
            )
        } ?: emptyList()

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
                OrderSummaryCard(detail)

                // 2 ─ Pilih metode pembayaran & Tombol Bayar
                PaymentMethodCard(
                    methods        = paymentMethods,
                    selected       = selectedPayment,
                    onSelect       = { selectedPayment = it },
                    onPayClick     = { viewModel.processCheckout(token, selectedPayment) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3 ─ Alert Info
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
            val selectedMethodObj = paymentMethods.find { it.id == selectedPayment }
            if (selectedMethodObj != null) {
                UploadProofModal(
                    selectedMethod = selectedMethodObj,
                    onDismiss      = { showModal = false },
                    onConfirm      = { uri ->
                        val file = uriToFile(context, uri)
                        viewModel.uploadPaymentProof(transactionId, file)
                    }
                )
            }
        }

        if (state is CheckoutState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
        if (state is CheckoutState.Error) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text((state as CheckoutState.Error).message, color = Color.Red, modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)).padding(16.dp))
            }
        }
    } else {
        if (state is CheckoutState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (state is CheckoutState.Error) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text((state as CheckoutState.Error).message, color = Color.Red)
            }
        }
    }

    if (showSuccessModal) {
        PaymentSuccessModal(onDone = { showSuccessModal = false; onCheckoutSuccess() })
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
private fun OrderSummaryCard(detail: CheckoutDetailResponse) {
    SectionCard {
        Text("Ringkasan Pesanan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
        Spacer(Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.Top) {
            // Gambar produk
            if (detail.product.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = detail.product.imageUrl,
                    contentDescription = "Foto Produk",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueLight)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.login_illustration),
                    contentDescription = "Foto Produk",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueLight)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    detail.product.name,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = DarkText,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    detail.purchaseLink.priceLabel,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = PrimaryBlue
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(Modifier.height(14.dp))

        MetaRow(label = "Seller", value = detail.seller.name, valueColor = PrimaryBlue)
        Spacer(Modifier.height(10.dp))

        MetaRow(label = "Batas Pembayaran", value = detail.purchaseLink.expiredAt ?: "-", valueColor = DarkText)

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(BlueLight).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Chat, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(10.dp))
            val noteText = if (!detail.purchaseLink.note.isNullOrEmpty()) detail.purchaseLink.note else "Harga telah disepakati. Silakan lakukan pembayaran sebelum batas waktu berakhir."
            Text(
                text = noteText,
                fontSize = 11.sp, color = MedText, lineHeight = 16.sp, modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(14.dp))
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text("Total Pembayaran", fontSize = 13.sp, color = MedText)
            Text(detail.purchaseLink.priceLabel, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
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
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, if (isSelected) PrimaryBlue else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .background(if (isSelected) BlueLight else Color.White)
                    .clickable { onSelect(method.id) }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        method.icon,
                        contentDescription = null,
                        tint = if (isSelected) PrimaryBlue else GrayText,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${method.type} - ${method.name}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = if (method.type.contains("Bank")) "No. Rekening: ${method.accountNumber}" else "No. HP: ${method.accountNumber}",
                        fontSize = 11.sp,
                        color = GrayText
                    )
                    Text(
                        text = "a/n: ${method.accountName}",
                        fontSize = 11.sp,
                        color = GrayText
                    )
                }
                if (isSelected) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(BlueLight)
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Outlined.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "Metode pembayaran ditentukan oleh seller. Pastikan kamu memilih metode yang tersedia agar pesananmu diproses dengan cepat.",
                fontSize = 11.sp, color = MedText, lineHeight = 16.sp, modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick   = onPayClick,
            modifier  = Modifier.fillMaxWidth().height(52.dp),
            shape     = RoundedCornerShape(12.dp),
            colors    = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text("Bayar Sekarang", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Upload Proof Modal ───────────────────────────────────────────────────────
@Composable
fun UploadProofModal(
    selectedMethod : PaymentMethod,
    onDismiss      : () -> Unit,
    onConfirm      : (Uri) -> Unit
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

                // Detail Rekening Seller
                Surface(
                    color = BlueLight,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Tujuan Transfer:", fontSize = 11.sp, color = MedText)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${selectedMethod.name} - ${selectedMethod.accountNumber}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "a.n. ${selectedMethod.accountName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkText
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

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
                                onConfirm(imageUri!!)
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

data class PaymentMethod(
    val id: String,
    val type: String,
    val name: String,
    val icon: ImageVector,
    val accountNumber: String = "",
    val accountName: String = ""
)

fun uriToFile(context: android.content.Context, uri: Uri): java.io.File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = java.io.File.createTempFile("proof", ".jpg", context.cacheDir)
    tempFile.outputStream().use { output ->
        inputStream?.copyTo(output)
    }
    return tempFile
}