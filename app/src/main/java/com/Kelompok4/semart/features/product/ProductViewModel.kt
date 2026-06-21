package com.Kelompok4.semart.features.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.model.Product
import com.Kelompok4.semart.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProductDetailState {
    object Idle : ProductDetailState()
    object Loading : ProductDetailState()
    data class Success(val product: Product) : ProductDetailState()
    data class Error(val message: String) : ProductDetailState()
}

class ProductViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {
    private val _state = MutableStateFlow<ProductDetailState>(ProductDetailState.Idle)
    val state: StateFlow<ProductDetailState> = _state

    fun loadProductDetail(id: Int) {
        _state.value = ProductDetailState.Loading
        viewModelScope.launch {
            repository.getProductDetail(id).fold(
                onSuccess = { _state.value = ProductDetailState.Success(it) },
                onFailure = { _state.value = ProductDetailState.Error(it.message ?: "Gagal memuat detail produk") }
            )
        }
    }

    fun reportProduct(id: Int, reason: String) {
        viewModelScope.launch {
            repository.sendReport(id, reason)
        }
    }
}
