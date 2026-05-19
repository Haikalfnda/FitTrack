package com.aplikasis.fittrack.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.ui.theme.DarkText
import com.aplikasis.fittrack.ui.theme.MutedText
import com.aplikasis.fittrack.ui.theme.PrimaryBlue
import com.aplikasis.fittrack.ui.theme.ScreenBg
import com.aplikasis.fittrack.ui.component.AuthTextField
import com.aplikasis.fittrack.ui.component.AuthTopBar

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text(
                    text = "Daftar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
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
                    modifier = Modifier.clickable {
                        onLoginClick()
                    }
                )
            }
        }
    }
}