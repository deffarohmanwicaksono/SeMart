package com.Kelompok4.semart.features.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.model.Transaction
import com.Kelompok4.semart.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TransactionState {
    object Idle : TransactionState()
    object Loading : TransactionState()
    data class Success(val transactions: List<Transaction>) : TransactionState()
    data class Error(val message: String) : TransactionState()
}

class TransactionViewModel(private val repository: TransactionRepository = TransactionRepository()) : ViewModel() {
    private val _state = MutableStateFlow<TransactionState>(TransactionState.Idle)
    val state: StateFlow<TransactionState> = _state

    fun loadPurchaseHistory() {
        _state.value = TransactionState.Loading
        viewModelScope.launch {
            repository.getPurchaseHistory().fold(
                onSuccess = { _state.value = TransactionState.Success(it) },
                onFailure = { _state.value = TransactionState.Error(it.message ?: "Gagal memuat riwayat transaksi") }
            )
        }
    }

    fun postReview(transactionId: Int, rating: Int, comment: String?) {
        _state.value = TransactionState.Loading
        viewModelScope.launch {
            repository.postReview(transactionId, rating, comment).fold(
                onSuccess = {
                    loadPurchaseHistory() // Refresh data
                },
                onFailure = {
                    _state.value = TransactionState.Error(it.message ?: "Gagal mengirim ulasan")
                }
            )
        }
    }
}
