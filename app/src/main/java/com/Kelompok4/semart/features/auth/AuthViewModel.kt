package com.Kelompok4.semart.features.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Kelompok4.semart.data.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val errorMessage: String) : LoginState()
}

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email dan password tidak boleh kosong")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            repository.login(email, password).fold(
                onSuccess = {
                    _loginState.value = LoginState.Success("Login Berhasil!")
                },
                onFailure = {
                    _loginState.value = LoginState.Error(it.message ?: "Login gagal")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _loginState.value = LoginState.Idle
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}