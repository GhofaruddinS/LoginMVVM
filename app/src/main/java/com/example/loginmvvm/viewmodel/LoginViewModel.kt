package com.example.loginmvvm.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginmvvm.data.local.entity.User
import com.example.loginmvvm.data.repository.UserRepository
import kotlinx.coroutines.launch

sealed interface LoginState {
    data object Idle : LoginState
    data object Success : LoginState
    data class Error(val message: String) : LoginState
}

class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var loginState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    init {
        insertDummyUser()
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            loginState = LoginState.Error("Username dan password tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            val user = repository.login(username.trim(), password)
            loginState = if (user != null) {
                LoginState.Success
            } else {
                LoginState.Error("Username atau Password Salah")
            }
        }
    }

    private fun insertDummyUser() {
        viewModelScope.launch {
            repository.insert(User(username = "admin", password = "12345"))
        }
    }
}
