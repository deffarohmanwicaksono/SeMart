package com.Kelompok4.semart.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.remote.ProfileResponse
import com.Kelompok4.semart.data.repository.AuthRepository
import com.Kelompok4.semart.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val profile: ProfileResponse, val transactionCount: Int) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val transactionRepository: TransactionRepository = TransactionRepository()
) : ViewModel() {
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val state: StateFlow<ProfileState> = _state

    fun loadProfile() {
        _state.value = ProfileState.Loading
        viewModelScope.launch {
            val profileResult = authRepository.getProfile()
            val transactionResult = transactionRepository.getPurchaseHistory()

            if (profileResult.isSuccess) {
                val profile = profileResult.getOrThrow()
                val transactions = transactionResult.getOrNull() ?: emptyList()
                val completedCount = transactions.count { it.status == "selesai" || it.status == "Selesai" }
                _state.value = ProfileState.Success(profile, completedCount)
            } else {
                _state.value = ProfileState.Error(profileResult.exceptionOrNull()?.message ?: "Gagal memuat profil")
            }
        }
    }
}
