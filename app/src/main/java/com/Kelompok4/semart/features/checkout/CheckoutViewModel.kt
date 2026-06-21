package com.Kelompok4.semart.features.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.remote.CheckoutDetailResponse
import com.Kelompok4.semart.data.repository.CheckoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    data class DetailSuccess(val detail: CheckoutDetailResponse) : CheckoutState()
    data class ProcessCheckoutSuccess(val response: com.Kelompok4.semart.data.remote.CheckoutResponse) : CheckoutState()
    data class UploadProofSuccess(val message: String) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

class CheckoutViewModel(private val repository: CheckoutRepository = CheckoutRepository()) : ViewModel() {
    private val _state = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val state: StateFlow<CheckoutState> = _state

    fun loadCheckoutDetail(token: String) {
        _state.value = CheckoutState.Loading
        viewModelScope.launch {
            repository.getCheckoutDetail(token).fold(
                onSuccess = { _state.value = CheckoutState.DetailSuccess(it) },
                onFailure = { _state.value = CheckoutState.Error(it.message ?: "Gagal memuat detail checkout") }
            )
        }
    }

    fun processCheckout(token: String, paymentMethod: String) {
        _state.value = CheckoutState.Loading
        viewModelScope.launch {
            repository.processCheckout(token, paymentMethod).fold(
                onSuccess = { _state.value = CheckoutState.ProcessCheckoutSuccess(it) },
                onFailure = { _state.value = CheckoutState.Error(it.message ?: "Gagal proses checkout") }
            )
        }
    }

    fun uploadPaymentProof(transactionId: Int, file: File) {
        _state.value = CheckoutState.Loading
        viewModelScope.launch {
            repository.uploadPaymentProof(transactionId, file).fold(
                onSuccess = { _state.value = CheckoutState.UploadProofSuccess(it) },
                onFailure = { _state.value = CheckoutState.Error(it.message ?: "Upload gagal") }
            )
        }
    }
}
