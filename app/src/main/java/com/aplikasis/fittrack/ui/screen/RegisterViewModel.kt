package com.aplikasis.fittrack.ui.screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aplikasis.fittrack.data.database.FitTrackDatabase
import com.aplikasis.fittrack.data.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = FitTrackDatabase.getDatabase(application).fitTrackDao()

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    fun register(nama: String, email: String, password: String, confirmPassword: String) {
        // Validasi input
        if (nama.isBlank() || email.isBlank() || password.isBlank()) {
            _state.value = RegisterState.Error("Semua field harus diisi.")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.value = RegisterState.Error("Format email tidak valid.")
            return
        }
        if (password.length < 6) {
            _state.value = RegisterState.Error("Kata sandi minimal 6 karakter.")
            return
        }
        if (password != confirmPassword) {
            _state.value = RegisterState.Error("Konfirmasi kata sandi tidak cocok.")
            return
        }

        viewModelScope.launch {
            _state.value = RegisterState.Loading
            try {
                val user = UserEntity(
                    nama = nama.trim(),
                    email = email.trim().lowercase(),
                    password = password,
                    role = "user",
                    status = "aktif"
                )
                dao.insertUser(user)
                _state.value = RegisterState.Success
            } catch (e: Exception) {
                // Kemungkinan email sudah terdaftar (unique constraint)
                _state.value = RegisterState.Error("Email sudah terdaftar. Gunakan email lain.")
            }
        }
    }

    fun resetState() {
        _state.value = RegisterState.Idle
    }
}