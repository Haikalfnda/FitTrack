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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    onKelolaKontenClick: () -> Unit,
    onKelolaVideoClick: () -> Unit,
    onDataPenggunaClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val viewModel = rememberAdminViewModel()

    val jumlahUserAktif by viewModel.jumlahUserAktif.collectAsState()
    val jumlahKonten by viewModel.jumlahKonten.collectAsState()
    val jumlahVideo by viewModel.jumlahVideo.collectAsState()
    val jumlahUserPending by viewModel.jumlahUserPending.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dashboard Admin",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = onLogoutClick) {
                        Text(
                            text = "Logout",
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.Bold
                        )
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
                .padding(20.dp)
        ) {
            Text(
                text = "Halo, Admin",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Kelola konten dan pengguna aplikasi.",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AdminMenuCard(
                    modifier = Modifier.weight(1f),
                    icon = "🗂️",
                    title = "Kelola Konten",
                    subtitle = "Edit banner, artikel, tips",
                    onClick = onKelolaKontenClick
                )

                AdminMenuCard(
                    modifier = Modifier.weight(1f),
                    icon = "▶️",
                    title = "Kelola Video",
                    subtitle = "Tambah & ubah video",
                    onClick = onKelolaVideoClick
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            AdminMenuCard(
                modifier = Modifier.fillMaxWidth(),
                icon = "👥",
                title = "Data Pengguna",
                subtitle = "Lihat dan ubah akun pengguna",
                badge = if (jumlahUserPending > 0) "$jumlahUserPending pending" else null,
                onClick = onDataPenggunaClick
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Ringkasan",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "• $jumlahUserAktif pengguna aktif")
                    if (jumlahUserPending > 0) {
                        Text(
                            text = "• $jumlahUserPending pendaftaran baru",
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(text = "• $jumlahVideo video tutorial")
                    Text(text = "• $jumlahKonten konten/artikel/panduan")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF4444)
                )
            ) {
                Text(
                    text = "Keluar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminMenuCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = Color(0xFFEAF1FF),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon)
                }

                if (badge != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = badge,
                            color = Color(0xFFE53935),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
