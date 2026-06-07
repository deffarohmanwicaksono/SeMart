package com.Kelompok4.semart.features.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Flag
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.Kelompok4.semart.R

// Konstanta Warna SeMart
val PrimaryBlue = Color(0xFF3B9DF8)
val DarkText = Color(0xFF243447)
val GrayText = Color(0xFF6B7280)
val BorderGray = Color(0xFFE5E7EB)
val SoftBlueBg = Color(0xFFF3F9FF)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onBackClick: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var isFavorite by remember { mutableStateOf(false) }
    var showReportModal by remember { mutableStateOf(false) }
    var showFullDesc by remember { mutableStateOf(false) }

    // Data mockup
    val title = "Laptop MacBook Air M1 2020"
    val price = "Rp 7.500.000"
    val condition = "Bekas Seperti Baru"
    val brand = "Apple"
    val year = "2021"
    val shortDesc = "MacBook Air M1 2020, performa masih kencang untuk kebutuhan kuliah, browsing, desain ringan, dan editing. Baterai awet, tidak pernah servis, semua fungsi normal."
    val fullDesc = """MacBook Air M1 2020 (Space Gray)
Laptop andalan dengan performa chip M1 yang masih sangat kencang untuk kebutuhan kuliah, browsing, desain grafis ringan, hingga editing video/foto.

Spesifikasi Singkat:
• RAM: 8GB Unified Memory
• SSD: 256GB (Super cepat)
• Layar: Retina Display 13.3 inci
• Baterai: Health di atas 90%

Kondisi:
• Body mulus 98%, tidak ada dent atau goresan berarti
• Layar bersih, tidak ada dead pixel
• Semua fitur berjalan normal
• Kelengkapan: Unit MacBook + Charger original

Alasan Dijual:
Upgrade ke seri Pro karena kebutuhan project yang lebih berat."""

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = DarkText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(0.dp)
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Tombol Chat
                    Button(
                        onClick = onChatClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Chat,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chat dengan Seller",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Wishlist
                        OutlinedButton(
                            onClick = { isFavorite = !isFavorite },
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isFavorite) PrimaryBlue else PrimaryBlue
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isFavorite) SoftBlueBg else Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isFavorite) PrimaryBlue else PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFavorite) "Tersimpan" else "Simpan",
                                color = if (isFavorite) PrimaryBlue else PrimaryBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Laporkan
                        OutlinedButton(
                            onClick = { showReportModal = true },
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryBlue),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Flag,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Laporkan",
                                color = PrimaryBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // ── IMAGE SECTION ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login_illustration),
                    contentDescription = "Foto Produk",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentScale = ContentScale.Fit
                )

                // Dot indicator
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    repeat(5) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == 0) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (index == 0) PrimaryBlue else Color(0xFFCBD5E1))
                        )
                    }
                }
            }

            Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // ── INFO UTAMA ──
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Kategori
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SoftBlueBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Elektronik",
                        color = PrimaryBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Judul
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Harga
                Text(
                    text = price,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Kondisi pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SoftBlueBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = condition,
                        color = PrimaryBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── SPEC GRID ──
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SpecItem(
                        icon = { Icon(Icons.Filled.Info, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp)) },
                        label = "Kondisi",
                        value = "Bekas Baik",
                        modifier = Modifier.weight(1f)
                    )
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFFE2E8F0)))
                    SpecItem(
                        icon = { Icon(Icons.Filled.Star, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp)) },
                        label = "Merek",
                        value = brand,
                        modifier = Modifier.weight(1f)
                    )
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFFE2E8F0)))
                    SpecItem(
                        icon = { Icon(Icons.Filled.CalendarMonth, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp)) },
                        label = "Tahun",
                        value = year,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── DESKRIPSI ──
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Text(
                        text = "Deskripsi",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (showFullDesc) fullDesc else shortDesc,
                        fontSize = 13.sp,
                        color = DarkText,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showFullDesc = !showFullDesc }
                    ) {
                        Text(
                            text = if (showFullDesc) "Tampilkan lebih sedikit" else "Lihat selengkapnya",
                            color = PrimaryBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── SELLER CARD ──
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    Text(
                        text = "Dijual oleh",
                        fontSize = 12.sp,
                        color = GrayText,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .clickable { }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(SoftBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AP",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Andi Pratama",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Terverifikasi",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "4.9",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(128 ulasan)",
                                    fontSize = 11.sp,
                                    color = GrayText
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = GrayText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Kampus UNS",
                                    fontSize = 11.sp,
                                    color = GrayText
                                )
                            }
                        }

                        Text(
                            text = "Lihat Profil",
                            fontSize = 12.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ── REPORT MODAL ──
    if (showReportModal) {
        ReportProductModal(
            productName = title,
            productPrice = price,
            sellerName = "Andi Pratama",
            reporterName = "Syifa Qurrota",
            onDismiss = { showReportModal = false },
            onSubmit = { reason ->
                // TODO: kirim laporan ke backend
                showReportModal = false
            }
        )
    }
}

// ==========================================
// SPEC ITEM
// ==========================================

@Composable
fun SpecItem(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = GrayText
        )
    }
}

// ==========================================
// REPORT PRODUCT MODAL
// ==========================================

@Composable
fun ReportProductModal(
    productName: String,
    productPrice: String,
    sellerName: String,
    reporterName: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    val isValid = reason.trim().length >= 10

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .wrapContentHeight()
        ) {
            Column {
                // ── Header modal ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Laporkan Produk",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "Bantu kami menjaga keamanan marketplace",
                            fontSize = 12.sp,
                            color = GrayText
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Tutup",
                            tint = GrayText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Divider(color = Color(0xFFE2E8F0))

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    // ── Kartu Info Produk ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.login_illustration),
                            contentDescription = "Foto Produk",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = productName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = productPrice,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Seller: $sellerName",
                                fontSize = 11.sp,
                                color = GrayText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Kartu Pelapor ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftBlueBg)
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = reporterName.take(2).uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Pelapor",
                                fontSize = 11.sp,
                                color = GrayText
                            )
                            Text(
                                text = reporterName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Input alasan ──
                    Text(
                        text = "Alasan Laporan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Wajib diisi (minimal 10 karakter)",
                        fontSize = 11.sp,
                        color = GrayText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 110.dp),
                        placeholder = {
                            Text(
                                text = "Jelaskan secara detail mengapa produk ini dilaporkan...",
                                fontSize = 13.sp,
                                color = GrayText.copy(alpha = 0.7f)
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 13.sp,
                            color = DarkText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        maxLines = 6
                    )

                    // Hitung karakter
                    Text(
                        text = "${reason.length} karakter",
                        fontSize = 11.sp,
                        color = if (isValid) GrayText else Color(0xFFFBBF24),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Tombol footer ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Batal",
                                color = GrayText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { if (isValid) onSubmit(reason) },
                            enabled = isValid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                disabledContainerColor = Color(0xFFE2E8F0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Kirim Laporan",
                                color = if (isValid) Color.White else GrayText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}