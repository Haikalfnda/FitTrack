package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aplikasis.fittrack.data.entity.UserEntity

/**
 * FITUR 2 - Perubahan DataPenggunaScreen:
 * - Tambah TabRow: "Semua" | "Pending" | "Ditolak"
 * - Tab "Pending": tampilkan user pending dengan tombol Approve & Reject
 * - Tab "Semua": tampilan existing dengan toggle aktif/nonaktif
 * - Tab "Ditolak": list user yang sudah ditolak
 * - Badge jumlah pending ditampilkan di tab label
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataPenggunaScreen(
    onBackClick: () -> Unit
) {
    val viewModel = rememberAdminViewModel()
    val daftarUser by viewModel.daftarUser.collectAsState()
    val daftarPending by viewModel.daftarUserPending.collectAsState()
    val jumlahPending by viewModel.jumlahUserPending.collectAsState()

    var keyword by remember { mutableStateOf("") }
    var userDetail by remember { mutableStateOf<UserEntity?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        "Semua",
        if (jumlahPending > 0) "Pending ($jumlahPending)" else "Pending",
        "Ditolak"
    )

    val hasilCariSemua = daftarUser
        .filter { it.status != "pending" && it.status != "rejected" }
        .filter {
            it.nama.contains(keyword, ignoreCase = true) ||
                    it.email.contains(keyword, ignoreCase = true)
        }

    val hasilCariDitolak = daftarUser
        .filter { it.status.equals("rejected", ignoreCase = true) }
        .filter {
            it.nama.contains(keyword, ignoreCase = true) ||
                    it.email.contains(keyword, ignoreCase = true)
        }

    val hasilCariPending = daftarPending.filter {
        it.nama.contains(keyword, ignoreCase = true) ||
                it.email.contains(keyword, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Data Pengguna", fontWeight = FontWeight.Bold)
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
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFF2563EB) else Color.Gray
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                when (selectedTab) {
                    0 -> {
                        // Tab Semua — tampilan existing
                        if (hasilCariSemua.isEmpty()) {
                            EmptyStateText("Belum ada pengguna aktif")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(hasilCariSemua) { user ->
                                    UserCard(
                                        user = user,
                                        onDetailClick = { userDetail = user },
                                        onUbahStatusClick = { viewModel.ubahStatusUser(user) }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // Tab Pending — tombol Approve & Reject
                        if (hasilCariPending.isEmpty()) {
                            EmptyStateText("Tidak ada pendaftaran yang menunggu persetujuan")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(hasilCariPending) { user ->
                                    PendingUserCard(
                                        user = user,
                                        onDetailClick = { userDetail = user },
                                        onApproveClick = { viewModel.approveUser(user) },
                                        onRejectClick = { viewModel.rejectUser(user) }
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // Tab Ditolak
                        if (hasilCariDitolak.isEmpty()) {
                            EmptyStateText("Tidak ada pendaftaran yang ditolak")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(hasilCariDitolak) { user ->
                                    RejectedUserCard(
                                        user = user,
                                        onDetailClick = { userDetail = user },
                                        // Bisa approve ulang dari ditolak
                                        onApproveClick = { viewModel.approveUser(user) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog detail pengguna
    if (userDetail != null) {
        AlertDialog(
            onDismissRequest = { userDetail = null },
            title = { Text(text = "Detail Pengguna") },
            text = {
                Column {
                    Text(text = "Nama: ${userDetail?.nama}")
                    Text(text = "Email: ${userDetail?.email}")
                    Text(text = "Level: ${userDetail?.level.takeIf { it?.isNotBlank() == true } ?: "-"}")
                    Text(text = "Tujuan: ${userDetail?.tujuan.takeIf { it?.isNotBlank() == true } ?: "-"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    val statusColor = when (userDetail?.status) {
                        "aktif" -> Color(0xFF0FA958)
                        "pending" -> Color(0xFFF59E0B)
                        "rejected" -> Color(0xFFE53935)
                        else -> Color.Gray
                    }
                    Row {
                        Text(text = "Status: ")
                        Text(
                            text = userDetail?.status?.replaceFirstChar { it.uppercase() } ?: "-",
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
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

// ── Card Components ───────────────────────────────────────────────────────────

@Composable
private fun UserAvatarBox(nama: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(color = Color(0xFFEAF1FF), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = nama.take(1).uppercase(),
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Bold
        )
    }
}

/** Card existing untuk user aktif/nonaktif */
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatarBox(user.nama)
            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.nama, fontWeight = FontWeight.Bold)
                Text(
                    text = user.email,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    TextButton(onClick = onDetailClick) { Text(text = "Detail") }
                    TextButton(onClick = onUbahStatusClick) {
                        Text(text = if (aktif) "Nonaktifkan" else "Aktifkan")
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

/**
 * FITUR 2: Card untuk user pending dengan tombol Approve (hijau) dan Reject (merah).
 */
@Composable
private fun PendingUserCard(
    user: UserEntity,
    onDetailClick: () -> Unit,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatarBox(user.nama)
                Spacer(modifier = Modifier.size(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = user.nama, fontWeight = FontWeight.Bold)
                    Text(
                        text = user.email,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Badge status pending
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "Pending",
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Detail", color = Color(0xFF2563EB))
                }

                // Tombol Approve
                Button(
                    onClick = onApproveClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0FA958)
                    )
                ) {
                    Text(text = "✓ Setujui", color = Color.White)
                }

                // Tombol Reject
                Button(
                    onClick = onRejectClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    )
                ) {
                    Text(text = "✕ Tolak", color = Color.White)
                }
            }
        }
    }
}

/** Card untuk user yang sudah ditolak, dengan opsi approve ulang. */
@Composable
private fun RejectedUserCard(
    user: UserEntity,
    onDetailClick: () -> Unit,
    onApproveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatarBox(user.nama)
            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.nama, fontWeight = FontWeight.Bold)
                Text(
                    text = user.email,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    TextButton(onClick = onDetailClick) { Text(text = "Detail") }
                    TextButton(onClick = onApproveClick) {
                        Text(text = "Setujui Ulang", color = Color(0xFF0FA958))
                    }
                }
            }

            Text(
                text = "Ditolak",
                color = Color(0xFFE53935),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyStateText(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = Color.Gray)
    }
}