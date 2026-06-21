package com.Kelompok4.semart.features.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.model.Wishlist
import com.Kelompok4.semart.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WishlistState {
    object Idle : WishlistState()
    object Loading : WishlistState()
    data class Success(val wishlist: List<Wishlist>) : WishlistState()
    data class Error(val message: String) : WishlistState()
}

class WishlistViewModel(private val repository: WishlistRepository = WishlistRepository()) : ViewModel() {
    private val _state = MutableStateFlow<WishlistState>(WishlistState.Idle)
    val state: StateFlow<WishlistState> = _state

    fun loadWishlist() {
        _state.value = WishlistState.Loading
        viewModelScope.launch {
            repository.getWishlist().fold(
                onSuccess = { _state.value = WishlistState.Success(it) },
                onFailure = { _state.value = WishlistState.Error(it.message ?: "Gagal memuat wishlist") }
            )
        }
    }

    fun toggleWishlist(productId: Int) {
        viewModelScope.launch {
            repository.toggleWishlist(productId).onSuccess {
                loadWishlist() // Reload after toggle
            }
        }
    }

    fun removeWishlist(productId: Int) {
        viewModelScope.launch {
            repository.removeWishlist(productId).onSuccess {
                loadWishlist()
            }
        }
    }

    fun clearWishlist() {
        viewModelScope.launch {
            repository.clearWishlist().onSuccess {
                loadWishlist()
            }
        }
    }
}
