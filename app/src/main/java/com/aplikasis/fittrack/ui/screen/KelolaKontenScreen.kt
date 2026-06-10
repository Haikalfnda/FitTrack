package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.data.database.FitTrackDatabase
import com.aplikasis.fittrack.data.entity.KontenEntity
import com.aplikasis.fittrack.ui.theme.PrimaryBlue
import com.aplikasis.fittrack.ui.theme.ScreenBg
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaKontenScreen(
    onBackClick: () -> Unit,
    onTambahClick: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val dao = remember {
        FitTrackDatabase.getDatabase(context).fitTrackDao()
    }

    val daftarKonten by dao.getAllKonten().collectAsState(initial = emptyList())

    val kategoriKonten = listOf("Banner", "Artikel", "Tips", "Program")
    var kategoriDipilih by rememberSaveable { mutableStateOf("Banner") }
    var kontenHapus by remember { mutableStateOf<KontenEntity?>(null) }

    val hasilFilter = daftarKonten.filter {
        it.kategori.equals(kategoriDipilih, ignoreCase = true)
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Kelola Konten",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = PrimaryBlue
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onTambahClick) {
                            Text(
                                text = "+",
                                color = PrimaryBlue,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )

                Divider(color = Color(0xFFE5E7EB))
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                kategoriKonten.forEach { kategori ->
                    KategoriChip(
                        text = kategori,
                        selected = kategoriDipilih == kategori,
                        onClick = { kategoriDipilih = kategori }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (hasilFilter.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada konten $kategoriDipilih",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(hasilFilter) { konten ->
                        KontenItemCard(
                            konten = konten,
                            onEditClick = {
                                onEditClick(konten.idKonten)
                            },
                            onHapusClick = {
                                kontenHapus = konten
                            }
                        )
                    }
                }
            }

            Button(
                onClick = onTambahClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text(
                    text = "Tambah Konten",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (kontenHapus != null) {
        AlertDialog(
            onDismissRequest = {
                kontenHapus = null
            },
            title = {
                Text(text = "Hapus Konten")
            },
            text = {
                Text(text = "Yakin ingin menghapus konten ini?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            kontenHapus?.let {
                                dao.deleteKonten(it)
                            }
                            kontenHapus = null
                        }
                    }
                ) {
                    Text(text = "Hapus")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        kontenHapus = null
                    }
                ) {
                    Text(text = "Batal")
                }
            }
        )
    }
}

@Composable
private fun KategoriChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryBlue,
            selectedLabelColor = Color.White,
            containerColor = Color(0xFFE8EEF7),
            labelColor = Color(0xFF4B5563)
        ),
        border = null
    )
}

@Composable
private fun KontenItemCard(
    konten: KontenEntity,
    onEditClick: () -> Unit,
    onHapusClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = konten.judul,
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = konten.isi.ifBlank { "Belum ada isi konten" },
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = "Edit",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            onEditClick()
                        }
                    )

                    Text(
                        text = " • ",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Text(
                        text = "Hapus",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            onHapusClick()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            StatusBadge(status = konten.status)
        }
    }
}

@Composable
private fun StatusBadge(
    status: String
) {
    val aktif = status.equals("Aktif", ignoreCase = true)

    val backgroundColor = if (aktif) {
        Color(0xFFD9FBE7)
    } else {
        Color(0xFFFFF0C2)
    }

    val textColor = if (aktif) {
        Color(0xFF0FA958)
    } else {
        Color(0xFFC98200)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}
