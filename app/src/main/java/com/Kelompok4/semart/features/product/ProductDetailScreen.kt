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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.Kelompok4.semart.R
import com.Kelompok4.semart.features.product.ProductViewModel
import com.Kelompok4.semart.features.product.ProductDetailState
import com.Kelompok4.semart.ui.theme.*
import com.Kelompok4.semart.features.wishlist.WishlistViewModel
import com.Kelompok4.semart.data.remote.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductViewModel = viewModel(),
    wishlistViewModel: WishlistViewModel = viewModel(),
    productId: Int = 0,
    onBackClick: () -> Unit = {},
    onChatClick: (sellerId: Int, productId: Int) -> Unit = { _, _ -> },
    onSellerClick: (sellerId: Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var isFavorite by remember { mutableStateOf(false) }
    var showReportModal by remember { mutableStateOf(false) }
    var showFullDesc by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }

    when (val currentState = state) {
        is ProductDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
        is ProductDetailState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(currentState.message, color = Color.Red)
            }
        }
        is ProductDetailState.Success -> {
            val product = currentState.product

            LaunchedEffect(product.id) {
                isFavorite = product.isWishlisted
            }

            val title = product.name
            val price = product.priceLabel
            val condition = product.condition.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
            val brand = product.category ?: "Lainnya"
            val shortDesc = product.description ?: "Tidak ada deskripsi"
            val fullDesc = product.description ?: "Tidak ada deskripsi"

            Scaffold(
                containerColor = Color(0xFFF8FAFC), // Menggunakan abu-abu sangat muda agar card kontras bersih
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
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            // Tombol Chat (Atas)
                            Button(
                                onClick = { onChatClick(product.seller?.id ?: 0, productId) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
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

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tombol Simpan & Laporkan (Bawah berjajar)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Wishlist
                                OutlinedButton(
                                    onClick = {
                                        isFavorite = !isFavorite
                                        wishlistViewModel.toggleWishlist(productId)
                                    },
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        PrimaryBlue
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
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isFavorite) "Tersimpan" else "Simpan",
                                        color = PrimaryBlue,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
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
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Laporkan",
                                        color = PrimaryBlue,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
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
                    val pagerState = rememberPagerState(pageCount = { if (product.images.isNotEmpty()) product.images.size else 1 })
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            if (product.images.isNotEmpty()) {
                                AsyncImage(
                                    model = product.images[page].url,
                                    contentDescription = "Foto Produk $page",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.login_illustration),
                                    contentDescription = "Foto Produk Default",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        if (product.images.size > 1) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(product.images.size) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                                            .clip(CircleShape)
                                            .background(if (pagerState.currentPage == index) PrimaryBlue else Color(0xFFCBD5E1))
                                    )
                                }
                            }
                        }
                    }

                    // ── INFO UTAMA ──
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Judul Produk
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            lineHeight = 26.sp
                        )

                        // Harga
                        Text(
                            text = price,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ── SPEC GRID (Kondisi & Kategori) ──
                    Surface(
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(top = 18.dp)) {
                            Text(
                                text = "Kondisi & Kategori",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SpecItem(
                                        icon = { Icon(Icons.Filled.Info, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp)) },
                                        label = "Kondisi",
                                        value = condition,
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Sekat (Divider)
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(36.dp)
                                            .background(Color(0xFFE2E8F0))
                                    )
                                    SpecItem(
                                        icon = { Icon(Icons.Filled.Star, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp)) },
                                        label = "Kategori",
                                        value = brand,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ── DESKRIPSI ──
                    Surface(
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
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
                                lineHeight = 22.sp,
                                maxLines = if (showFullDesc) Int.MAX_VALUE else 3,
                                overflow = TextOverflow.Ellipsis
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── SELLER CARD ──
                    Surface(
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
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
                                    .clickable { onSellerClick(product.seller?.id ?: 0) }
                                    .padding(14.dp),
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
                                        text = product.seller?.name?.take(2)?.uppercase() ?: "SL",
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = product.seller?.name ?: "Seller",
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
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${product.sellerRating ?: "0.0"}",
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
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "(${product.sellerReviewsCount ?: 0} ulasan)",
                                            fontSize = 11.sp,
                                            color = GrayText
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = GrayText,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
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

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // ── REPORT MODAL ──
            if (showReportModal) {
                ReportProductModal(
                    productName = title,
                    productPrice = price,
                    sellerName = product.seller?.name ?: "Seller",
                    reporterName = SessionManager.getName() ?: "User",
                    onDismiss = { showReportModal = false },
                    onSubmit = { reason ->
                        viewModel.reportProduct(productId, reason)
                        showReportModal = false
                    }
                )
            }
        }
        else -> {}
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
    Row(
        modifier = modifier.padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = GrayText,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SoftBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sellerName.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                fontSize = 16.sp
                            )
                        }
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

                    Text(
                        text = "${reason.length} karakter",
                        fontSize = 11.sp,
                        color = if (isValid) GrayText else Color(0xFFFBBF24),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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