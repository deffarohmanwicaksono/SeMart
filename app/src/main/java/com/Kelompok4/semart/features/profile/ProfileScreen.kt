package com.Kelompok4.semart.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── DATA CLASS & SEEDER ──
data class UserProfile(
    val name: String,
    val email: String,
    val transactionCount: Int,
    val status: String,
    val phoneNumber: String,
    val joinedDate: String
)

val DummyUserProfile = UserProfile(
    name = "Andi Pratama",
    email = "andi.pratama@student.uns.ac.id",
    transactionCount = 156,
    status = "Aktif",
    phoneNumber = "081234567890",
    joinedDate = "12 Jan 2024"
)

// Konstanta Warna SeMart
val PrimaryBlue = Color(0xFF3B9DF8)
val DarkText = Color(0xFF243447)
val GrayText = Color(0xFF6B7280)
val BorderGray = Color(0xFFE5E7EB)
val SoftBlueBg = Color(0xFFF3F9FF)
val DangerRed = Color(0xFFEF4444)
val SoftRedBg = Color(0xFFFFF1F1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile = DummyUserProfile,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onTransactionHistoryClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Keluar dari Akun?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkText
                )
            },
            text = {
                Text(
                    text = "Kamu akan keluar dari akun SeMart. Pastikan semua aktivitasmu sudah selesai.",
                    fontSize = 13.sp,
                    color = GrayText,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ya, Keluar", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Batal", fontSize = 13.sp, color = GrayText)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White, // Background bersih putih
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 0.dp,
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Panah kembali minimalis
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = DarkText,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Profil Saya",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGray, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ProfileNavItem(selected = false, icon = Icons.Filled.Home, label = "Beranda", onClick = onHomeClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ProfileNavItem(selected = false, icon = Icons.Filled.Search, label = "Cari", onClick = onSearchClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ProfileNavItem(selected = false, icon = Icons.Filled.Chat, label = "Chat", onClick = onChatClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        ProfileNavItem(selected = true, icon = Icons.Filled.Person, label = "Profil")
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── HERO CARD: Avatar + Nama + Email ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circle yang rapi dan bersih
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(SoftBlueBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Avatar",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = userProfile.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = userProfile.email,
                    fontSize = 13.sp,
                    color = GrayText
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Transaksi Selesai badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SoftBlueBg)
                        .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${userProfile.transactionCount} Transaksi Selesai",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── SECTION: Informasi Akun ──
            ProfileSectionCard {
                Text(
                    text = "Informasi Akun",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GrayText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                HorizontalDivider(color = BorderGray, thickness = 1.dp)

                ProfileInfoRow(
                    icon = Icons.Outlined.Shield,
                    label = "Status",
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (userProfile.status == "Aktif") Color(0xFFE8F8F0) else SoftRedBg)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = userProfile.status,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (userProfile.status == "Aktif") Color(0xFF16A34A) else DangerRed
                            )
                        }
                    },
                    showDivider = true
                )

                ProfileInfoRow(
                    icon = Icons.Outlined.Phone,
                    label = "No. HP",
                    value = userProfile.phoneNumber,
                    showDivider = true
                )

                ProfileInfoRow(
                    icon = Icons.Outlined.CalendarToday,
                    label = "Bergabung",
                    value = userProfile.joinedDate,
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── SECTION: Aktivitas ──
            ProfileSectionCard {
                Text(
                    text = "Aktivitas",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GrayText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                HorizontalDivider(color = BorderGray, thickness = 1.dp)

                ProfileMenuRow(
                    icon = Icons.Outlined.Receipt,
                    iconBg = SoftBlueBg,
                    iconTint = PrimaryBlue,
                    title = "Riwayat Transaksi",
                    subtitle = "Lihat semua transaksi pembelian & penjualan",
                    onClick = onTransactionHistoryClick,
                    showDivider = true
                )

                ProfileMenuRow(
                    icon = Icons.Outlined.FavoriteBorder,
                    iconBg = SoftBlueBg,
                    iconTint = PrimaryBlue,
                    title = "Wishlist Saya",
                    subtitle = "Produk yang kamu simpan",
                    onClick = onWishlistClick,
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── SECTION: Pengaturan & Logout ──
            ProfileSectionCard {
                Text(
                    text = "Pengaturan",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GrayText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )

                HorizontalDivider(color = BorderGray, thickness = 1.dp)

                ProfileMenuRow(
                    icon = Icons.Outlined.Notifications,
                    iconBg = SoftBlueBg,
                    iconTint = PrimaryBlue,
                    title = "Notifikasi",
                    subtitle = "Kelola preferensi notifikasi",
                    onClick = onNotificationClick,
                    showDivider = true
                )

                // Tombol logout
                ProfileMenuRow(
                    icon = Icons.Outlined.Logout,
                    iconBg = SoftBlueBg,
                    iconTint = PrimaryBlue,
                    title = "Keluar Akun",
                    subtitle = "Akhiri sesi SeMart kamu saat ini",
                    onClick = { showLogoutDialog = true },
                    showDivider = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── REUSABLE: Section Card Wrapper ──
@Composable
fun ProfileSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

// ── REUSABLE: Profile Info Row (teks value) ──
@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String = "",
    trailingContent: (@Composable () -> Unit)? = null,
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SoftBlueBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            color = GrayText,
            modifier = Modifier.weight(1f)
        )

        if (trailingContent != null) {
            trailingContent()
        } else {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )
        }
    }

    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = BorderGray,
            thickness = 1.dp
        )
    }
}

// ── REUSABLE: Profile Menu Row (navigable) ──
@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = GrayText,
                lineHeight = 15.sp
            )
        }

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFA1ACB8),
            modifier = Modifier.size(18.dp)
        )
    }

    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = BorderGray,
            thickness = 1.dp
        )
    }
}

// ── REUSABLE: Nav Item ──
@Composable
fun ProfileNavItem(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(height = 64.dp, width = 62.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) PrimaryBlue else Color(0xFFA1ACB8),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (selected) PrimaryBlue else GrayText,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}