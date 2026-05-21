package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aplikasis.fittrack.data.entity.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataPenggunaScreen(
    onBackClick: () -> Unit
) {
    val viewModel = rememberAdminViewModel()
    val daftarUser by viewModel.daftarUser.collectAsState()

    var keyword by remember { mutableStateOf("") }
    var userDetail by remember { mutableStateOf<UserEntity?>(null) }

    val hasilCari = daftarUser.filter {
        it.nama.contains(keyword, ignoreCase = true) ||
                it.email.contains(keyword, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Data Pengguna",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(text = "‹", style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        },
        containerColor = Color(0xFFF6F8FC)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text("Cari nama atau email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(hasilCari) { user ->
                    UserCard(
                        user = user,
                        onDetailClick = {
                            userDetail = user
                        },
                        onUbahStatusClick = {
                            viewModel.ubahStatusUser(user)
                        }
                    )
                }
            }
        }
    }

    if (userDetail != null) {
        AlertDialog(
            onDismissRequest = { userDetail = null },
            title = {
                Text(text = "Detail Pengguna")
            },
            text = {
                Column {
                    Text(text = "Nama: ${userDetail?.nama}")
                    Text(text = "Email: ${userDetail?.email}")
                    Text(text = "Level: ${userDetail?.level}")
                    Text(text = "Tujuan: ${userDetail?.tujuan}")
                    Text(text = "Status: ${userDetail?.status}")
                }
            },
            confirmButton = {
                TextButton(onClick = { userDetail = null }) {
                    Text(text = "Tutup")
                }
            }
        )
    }
}

@Composable
private fun UserCard(
    user: UserEntity,
    onDetailClick: () -> Unit,
    onUbahStatusClick: () -> Unit
) {
    val aktif = user.status.equals("aktif", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFEAF1FF),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.nama.take(1).uppercase(),
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.nama,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user.email,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row {
                    TextButton(onClick = onDetailClick) {
                        Text(text = "Detail")
                    }

                    TextButton(onClick = onUbahStatusClick) {
                        Text(
                            text = if (aktif) "Nonaktifkan" else "Aktifkan"
                        )
                    }
                }
            }

            Text(
                text = if (aktif) "Aktif" else "Nonaktif",
                color = if (aktif) Color(0xFF0FA958) else Color(0xFFE53935),
                fontWeight = FontWeight.Bold
            )
        }
    }
}