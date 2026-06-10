package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.data.database.FitTrackDatabase
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.ui.theme.PrimaryBlue
import com.aplikasis.fittrack.ui.theme.ScreenBg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * FITUR 2 & 3 - Perubahan LoginScreen:
 * - Cek status "pending" → tampilkan halaman menunggu persetujuan
 * - Cek status "rejected" → tampilkan halaman ditolak
 * - Status "nonaktif" → pesan error seperti sebelumnya
 * - Status "aktif" → lanjut ke onLoginSuccess
 */
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: (UserEntity) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val dao = remember {
        FitTrackDatabase.getDatabase(context).fitTrackDao()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isAdminMode by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // FITUR 2: State untuk menampilkan halaman status akun
    var pendingUser by remember { mutableStateOf<UserEntity?>(null) }
    var rejectedUser by remember { mutableStateOf<UserEntity?>(null) }

    // Tampilkan halaman "Menunggu Persetujuan" jika pending
    if (pendingUser != null) {
        AkunPendingScreen(
            namaUser = pendingUser!!.nama,
            onKembali = { pendingUser = null }
        )
        return
    }

    // Tampilkan halaman "Akun Ditolak" jika rejected
    if (rejectedUser != null) {
        AkunRejectedScreen(
            namaUser = rejectedUser!!.nama,
            onKembali = { rejectedUser = null }
        )
        return
    }

    Scaffold(containerColor = ScreenBg) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAdminMode) "Masuk Admin" else "Masuk",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
            }

            Divider(color = Color(0xFFE5E7EB))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = if (isAdminMode) "Login Admin FitTrack" else "Selamat datang kembali",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isAdminMode)
                        "Masukkan email dan kata sandi admin untuk mengelola aplikasi."
                    else
                        "Masuk untuk mencatat latihan dan memantau progres.",
                    fontSize = 14.sp,
                    color = Color(0xFF7C8BA1)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Email",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = "" },
                    placeholder = {
                        Text(
                            text = if (isAdminMode) "Masukkan email admin" else "Masukkan email",
                            color = Color(0xFF9CA3AF)
                        )
                    },
                    leadingIcon = { Text(text = "✉️") },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = PrimaryBlue,
                        unfocusedIndicatorColor = Color(0xFFD1D5DB),
                        cursorColor = PrimaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Kata Sandi",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = "" },
                    placeholder = {
                        Text(
                            text = if (isAdminMode) "Masukkan kata sandi admin" else "Masukkan kata sandi",
                            color = Color(0xFF9CA3AF)
                        )
                    },
                    leadingIcon = { Text(text = "🔒") },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = PrimaryBlue,
                        unfocusedIndicatorColor = Color(0xFFD1D5DB),
                        cursorColor = PrimaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = { /* lupa password belum dibuat */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Lupa kata sandi?",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                if (errorMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Email dan kata sandi wajib diisi"
                        } else {
                            scope.launch {
                                val user = withContext(Dispatchers.IO) {
                                    dao.login(email.trim().lowercase(), password.trim())
                                }

                                when {
                                    user == null -> {
                                        errorMessage = "Email atau kata sandi salah"
                                    }

                                    // FITUR 2: Cek status pending
                                    user.status.equals("pending", ignoreCase = true) -> {
                                        pendingUser = user
                                    }

                                    // FITUR 2: Cek status rejected
                                    user.status.equals("rejected", ignoreCase = true) -> {
                                        rejectedUser = user
                                    }

                                    user.status.equals("nonaktif", ignoreCase = true) -> {
                                        errorMessage = "Akun kamu sedang nonaktif"
                                    }

                                    isAdminMode && user.role != "admin" -> {
                                        errorMessage = "Akun ini bukan akun admin"
                                    }

                                    !isAdminMode && user.role == "admin" -> {
                                        errorMessage = "Silakan gunakan tombol Masuk sebagai Admin"
                                    }

                                    else -> {
                                        onLoginSuccess(user)
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        text = if (isAdminMode) "Masuk Admin" else "Masuk",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        isAdminMode = !isAdminMode
                        email = ""
                        password = ""
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryBlue
                    )
                ) {
                    Text(
                        text = if (isAdminMode) "Masuk sebagai Pengguna" else "Masuk sebagai Admin",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                TextButton(
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Belum punya akun? Daftar",
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ── Status Screens ────────────────────────────────────────────────────────────

/**
 * FITUR 2: Halaman yang ditampilkan saat user login dengan status "pending".
 */
@Composable
fun AkunPendingScreen(
    namaUser: String,
    onKembali: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "⏳", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Menunggu Persetujuan",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Halo $namaUser, akun kamu sedang dalam proses peninjauan oleh admin. Kamu akan bisa masuk setelah disetujui.",
            fontSize = 14.sp,
            color = Color(0xFF7C8BA1),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onKembali,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(
                text = "Kembali ke Login",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * FITUR 2: Halaman yang ditampilkan saat user login dengan status "rejected".
 */
@Composable
fun AkunRejectedScreen(
    namaUser: String,
    onKembali: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "❌", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Pendaftaran Ditolak",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Maaf $namaUser, pendaftaran kamu tidak dapat disetujui oleh admin. Silakan hubungi admin untuk informasi lebih lanjut.",
            fontSize = 14.sp,
            color = Color(0xFF7C8BA1),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onKembali,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935)
            )
        ) {
            Text(
                text = "Kembali ke Login",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
