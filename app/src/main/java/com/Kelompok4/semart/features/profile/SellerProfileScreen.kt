package com.Kelompok4.semart.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Kelompok4.semart.data.remote.SellerProfileResponse
import com.Kelompok4.semart.data.repository.ProductRepository
import com.Kelompok4.semart.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SellerProfileState {
    object Idle : SellerProfileState()
    object Loading : SellerProfileState()
    data class Success(val profile: SellerProfileResponse) : SellerProfileState()
    data class Error(val message: String) : SellerProfileState()
}

class SellerProfileViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {
    private val _state = MutableStateFlow<SellerProfileState>(SellerProfileState.Idle)
    val state: StateFlow<SellerProfileState> = _state

    fun loadSellerProfile(sellerId: Int) {
        _state.value = SellerProfileState.Loading
        viewModelScope.launch {
            repository.getSellerProfile(sellerId).fold(
                onSuccess = { _state.value = SellerProfileState.Success(it) },
                onFailure = { _state.value = SellerProfileState.Error(it.message ?: "Gagal memuat profil seller") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileScreen(
    sellerId: Int,
    onBackClick: () -> Unit,
    viewModel: SellerProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(sellerId) {
        viewModel.loadSellerProfile(sellerId)
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = Color(0xFFF8FAFC),
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

                        // Judul dan Subjudul
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Profil Seller",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                            Text(
                                text = "Informasi lengkap dan ulasan tentang penjual.",
                                fontSize = 12.sp,
                                color = GrayText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        when (val currentState = state) {
            is SellerProfileState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is SellerProfileState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(currentState.message, color = Color.Red)
                }
            }
            is SellerProfileState.Success -> {
                val seller = currentState.profile.seller
                val reviews = currentState.profile.reviews

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                ) {
                    item {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(SoftBlueBg)
                                            .border(1.dp, Color(0xFFE2E8F0), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(40.dp))
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = seller.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkText
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(SoftBlueBg)
                                            .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Verified, null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Seller Terverifikasi", color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.CalendarToday, null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "Bergabung ${seller.joined ?: "-"}",
                                            fontSize = 12.sp,
                                            color = GrayText
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                    Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Left Stat
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier.size(40.dp).clip(CircleShape).background(SoftBlueBg),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Outlined.Inventory2, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(seller.soldCount.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                                Text("Barang Terjual", fontSize = 12.sp, color = GrayText)
                                            }
                                        }

                                        // Vertical Divider
                                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFE2E8F0)))

                                        // Right Stat
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier.size(40.dp).clip(CircleShape).background(SoftBlueBg),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Outlined.StarOutline, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(seller.rating.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Row {
                                                        repeat(5) {
                                                            Icon(Icons.Outlined.StarOutline, null, tint = PrimaryBlue, modifier = Modifier.size(12.dp))
                                                        }
                                                    }
                                                }
                                                Text("${seller.reviewsCount} Ulasan Pembeli", fontSize = 12.sp, color = GrayText)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (reviews.isNotEmpty()) {
                            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Ulasan Pembeli", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                    
                    if (reviews.isNotEmpty()) {
                        items(reviews) { review ->
                            Surface(color = Color.White, modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(review.buyerName ?: "Pembeli", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Star, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(review.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(review.productName ?: "Produk", fontSize = 11.sp, color = GrayText)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(review.comment ?: "Tidak ada komentar", fontSize = 13.sp, color = DarkText)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                                }
                            }
                        }
                    } else {
                        item {
                            Surface(color = Color.White, modifier = Modifier.fillMaxWidth().height(100.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Belum ada ulasan.", color = GrayText, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}
