package com.Kelompok4.semart.features.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.Kelompok4.semart.R
import com.Kelompok4.semart.data.model.Product
import com.Kelompok4.semart.features.search.SearchViewModel
import com.Kelompok4.semart.features.search.SearchState
import com.Kelompok4.semart.ui.theme.*

import com.Kelompok4.semart.features.wishlist.WishlistViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    wishlistViewModel: WishlistViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProductClick: (Int) -> Unit = {},
    onChatClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    // State Dropdown Urutkan
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Terbaru") }

    // State untuk daftar riwayat pencarian
    val recentSearches = remember {
        mutableStateListOf("Kipas angin", "Tas", "Cermin", "Lampu belajar")
    }

    val sortOptions = listOf("Termurah", "Termahal", "Terbaru", "Terlama")

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp, // Sedikit bayangan untuk memisahkan topbar dengan konten
                modifier = Modifier.statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Kembali
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Kembali", tint = DarkText)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // SEARCH BAR
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it 
                            if(it.isNotEmpty()) {
                                viewModel.searchProducts(it, null, if(selectedSort == "Terbaru") null else selectedSort.lowercase())
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkText),
                        placeholder = {
                            Text("Cari barang kos...", fontSize = 13.sp, color = GrayText)
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Hapus teks", tint = GrayText, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = PrimaryBlue,
                            unfocusedIndicatorColor = BorderGray,
                            cursorColor = PrimaryBlue
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // IKON FILTER
                    Box {
                        IconButton(
                            onClick = { sortMenuExpanded = true },
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = "Urutkan",
                                tint = Color.White
                            )
                        }

                        // Dropdown Opsi Urutkan
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false },
                            modifier = Modifier.background(Color.White),
                            offset = DpOffset(x = 0.dp, y = 4.dp)
                        ) {
                            sortOptions.forEach { option ->
                                val isSelected = selectedSort == option
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            fontSize = 13.sp,
                                            color = if (isSelected) PrimaryBlue else DarkText,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedSort = option
                                        sortMenuExpanded = false
                                        if(searchQuery.isNotEmpty()) {
                                            viewModel.searchProducts(searchQuery, null, if(option == "Terbaru") null else option.lowercase())
                                        }
                                    }
                                )
                            }
                        }
                    }
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
                        SearchNavItemManual(selected = false, icon = Icons.Filled.Home, label = "Beranda", onClick = onHomeClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        SearchNavItemManual(selected = true, icon = Icons.Filled.Search, label = "Cari")
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        SearchNavItemManual(selected = false, icon = Icons.Filled.Chat, label = "Chat", onClick = onChatClick)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        SearchNavItemManual(selected = false, icon = Icons.Filled.Person, label = "Profil", onClick = onProfileClick)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Column {
                    if (searchQuery.isEmpty()) {
                        // TAMPILAN KETIKA PENCARIAN KOSONG
                        if (recentSearches.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Pencarian Terakhir", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                Text(
                                    text = "Hapus Semua",
                                    fontSize = 11.sp,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.clickable { recentSearches.clear() }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            recentSearches.forEach { searchHint ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            searchQuery = searchHint 
                                            viewModel.searchProducts(searchHint, null, if(selectedSort == "Terbaru") null else selectedSort.lowercase())
                                        }
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.History, contentDescription = null, tint = GrayText, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(searchHint, fontSize = 13.sp, color = DarkText)
                                    }

                                    IconButton(
                                        onClick = { recentSearches.remove(searchHint) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.Close, contentDescription = "Hapus", tint = GrayText, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    } else {
                        // TAMPILAN KETIKA ADA HASIL PENCARIAN
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Hasil Pencarian: \"$searchQuery\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            // GRID ITEM PRODUK
            if (searchQuery.isNotEmpty()) {
                when (state) {
                    is SearchState.Loading -> {
                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = PrimaryBlue)
                            }
                        }
                    }
                    is SearchState.Error -> {
                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                Text((state as SearchState.Error).message, color = Color.Red)
                            }
                        }
                    }
                    is SearchState.Success -> {
                        val products = (state as SearchState.Success).products
                        if (products.isEmpty()) {
                            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                    Text("Produk tidak ditemukan.", color = GrayText)
                                }
                            }
                        } else {
                            items(products) { product ->
                                ProductCardItem(
                                    product = product,
                                    onLikeClick = {
                                        scope.launch {
                                            wishlistViewModel.toggleWishlist(product.id)
                                            // Panggil ulang pencarian untuk merefresh state
                                            viewModel.searchProducts(searchQuery, null, if(selectedSort == "Terbaru") null else selectedSort.lowercase())
                                        }
                                    },
                                    modifier = Modifier.clickable { onProductClick(product.id) }
                                )
                            }
                        }
                    }
                    else -> {}
                }

                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// Item Navigasi Manual
@Composable
fun SearchNavItemManual(
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

// Component Reusable: Product Card
@Composable
fun ProductCardItem(
    product: Product,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                if (product.images.isNotEmpty()) {
                    AsyncImage(
                        model = product.images.first().url,
                        contentDescription = "Foto Produk",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(Color(0xFFF8FAFC)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.login_illustration),
                        contentDescription = "Foto Produk Default",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(Color(0xFFF8FAFC)),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = product.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = product.priceLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SoftBlueBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = product.condition.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F9FF))
                    .clickable { onLikeClick() }
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = if (product.isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Suka",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}