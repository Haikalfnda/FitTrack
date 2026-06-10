package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aplikasis.fittrack.data.entity.RiwayatLatihanEntity

// KODE BARU YANG BENAR:
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatLatihanScreen(
    navController: NavController,
    viewModel: RiwayatLatihanViewModel // <-- Tambahkan parameter ini agar match dengan NavGraph!
) {
    // ...
    // Ambil state filter dan data list langsung dari database secara real-time
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val dataRiwayat by viewModel.dataRiwayatState.collectAsState()
    var detailRiwayat by remember { mutableStateOf<RiwayatLatihanEntity?>(null) }

    Scaffold(
        containerColor = Color(0xFFF5F7FB),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF2F66EB), Color(0xFF6D79FF))
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Riwayat Latihan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // FILTER BUTTON
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Harian", "Mingguan", "Bulanan").forEach { filterText ->
                    FilterButton(
                        text = filterText,
                        selected = selectedFilter == filterText
                    ) {
                        viewModel.changeFilter(filterText) // Merubah filter di ViewModel
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // JIKA DATA KOSONG
            if (dataRiwayat.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada riwayat latihan untuk kategori ini.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                // JIKA DATA ADA
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(dataRiwayat) { item ->
                        RiwayatCard(
                            item = item,
                            onDetailClick = { detailRiwayat = item }
                        )
                    }
                }
            }
        }
    }

    detailRiwayat?.let { item ->
        AlertDialog(
            onDismissRequest = { detailRiwayat = null },
            title = { Text(text = item.namaProgram, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Tanggal: ${item.tanggal}")
                    Text(text = "Durasi: ${item.durasi}")
                    Text(text = "Reps: ${item.reps}")
                    Text(text = "Kalori: ${item.kalori}")
                    HorizontalDivider()
                    Text(text = item.detail.ifBlank { "Belum ada detail gerakan." })
                }
            },
            confirmButton = {
                TextButton(onClick = { detailRiwayat = null }) {
                    Text(text = "Tutup")
                }
            }
        )
    }
}

@Composable
fun FilterButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF3366FF) else Color(0xFFE4E9F2)
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else Color.DarkGray
        )
    }
}

@Composable
fun RiwayatCard(item: RiwayatLatihanEntity, onDetailClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.namaProgram,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Lihat Detail",
                    color = Color(0xFF3366FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onDetailClick() }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "${item.tanggal} • ${item.durasi}", color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoChip(text = item.reps, background = Color(0xFFDCE8FF), textColor = Color(0xFF3366FF))
                InfoChip(text = item.kalori, background = Color(0xFFFFEDBF), textColor = Color(0xFFFF9800))
            }
        }
    }
}

@Composable
fun InfoChip(text: String, background: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
