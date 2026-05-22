package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplikasis.fittrack.ui.component.AuthTextField
import com.aplikasis.fittrack.ui.component.AuthTopBar
import com.aplikasis.fittrack.ui.theme.*

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val state by viewModel.state.collectAsState()

    // Kalau register berhasil → langsung ke halaman login
    LaunchedEffect(state) {
        if (state is RegisterState.Success) {
            viewModel.resetState()
            onLoginClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        AuthTopBar(
            title = "Daftar",
            showBack = true,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Buat akun baru",
                color = DarkText,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Lengkapi data berikut untuk mendaftar.",
                color = MutedText,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            AuthTextField(
                label = "Nama Lengkap",
                value = name,
                onValueChange = { name = it },
                placeholder = "Masukkan nama",
                icon = Icons.Outlined.Person,
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuthTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                placeholder = "Masukkan email",
                icon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuthTextField(
                label = "Kata Sandi",
                value = password,
                onValueChange = { password = it },
                placeholder = "Buat kata sandi",
                icon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuthTextField(
                label = "Konfirmasi Kata Sandi",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Ulangi kata sandi",
                icon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            // Tampilkan pesan error kalau ada
            if (state is RegisterState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = (state as RegisterState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.register(name, email, password, confirmPassword)
                },
                enabled = state !is RegisterState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (state is RegisterState.Loading) {
                    CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Daftar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    color = PrimaryBlue.copy(alpha = 0.65f),
                    fontSize = 13.sp
                )
                Text(
                    text = "Masuk",
                    color = PrimaryBlue,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}