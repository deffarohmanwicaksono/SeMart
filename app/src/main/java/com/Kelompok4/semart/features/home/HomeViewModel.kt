package com.Kelompok4.semart.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.model.Product
import com.Kelompok4.semart.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Idle : HomeState()
    object Loading : HomeState()
    data class Success(val products: List<Product>) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Idle)
    val state: StateFlow<HomeState> = _state

    init {
        loadHomeProducts()
    }

    fun loadHomeProducts(category: String? = null, sort: String? = null) {
        _state.value = HomeState.Loading
        viewModelScope.launch {
            repository.getProducts(category, sort).fold(
                onSuccess = { _state.value = HomeState.Success(it) },
                onFailure = { _state.value = HomeState.Error(it.message ?: "Gagal memuat produk home") }
            )
        }
    }
}