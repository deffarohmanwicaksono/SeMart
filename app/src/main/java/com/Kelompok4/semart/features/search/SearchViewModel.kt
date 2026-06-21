package com.Kelompok4.semart.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.model.Product
import com.Kelompok4.semart.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val products: List<Product>) : SearchState()
    data class Error(val message: String) : SearchState()
}

class SearchViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {
    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state

    fun searchProducts(keyword: String, category: String? = null, sort: String? = null) {
        _state.value = SearchState.Loading
        viewModelScope.launch {
            repository.searchProducts(keyword, category, sort).fold(
                onSuccess = { _state.value = SearchState.Success(it) },
                onFailure = { _state.value = SearchState.Error(it.message ?: "Gagal mencari produk") }
            )
        }
    }
}
