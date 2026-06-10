package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aplikasis.fittrack.data.database.FitTrackDatabase
import com.aplikasis.fittrack.data.entity.KontenEntity
import com.aplikasis.fittrack.ui.theme.PrimaryBlue
import com.aplikasis.fittrack.ui.theme.ScreenBg
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormKontenScreen(
    idKonten: Long,
    onBackClick: () -> Unit,
    onSaved: () -> Unit = onBackClick
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { FitTrackDatabase.getDatabase(context).fitTrackDao() }

    val kategoriOptions = listOf("Banner", "Artikel", "Tips", "Program")
    val statusOptions = listOf("Aktif", "Nonaktif")

    var judul by rememberSaveable { mutableStateOf("") }
    var isi by rememberSaveable { mutableStateOf("") }
    var kategori by rememberSaveable { mutableStateOf("Banner") }
    var status by rememberSaveable { mutableStateOf("Aktif") }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(idKonten) {
        if (idKonten != 0L) {
            dao.getKontenById(idKonten)?.let { konten ->
                judul = konten.judul
                isi = konten.isi
                kategori = konten.kategori
                status = if (konten.status.equals("Aktif", ignoreCase = true)) "Aktif" else "Nonaktif"
            }
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = if (idKonten == 0L) "Tambah Konten" else "Edit Konten",
                            fontWeight = FontWeight.Bold
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
                    }
                )
                HorizontalDivider(color = Color(0xFFE5E7EB))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it },
                label = { Text("Judul") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Kategori",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    kategoriOptions.forEach { option ->
                        OptionChip(
                            text = option,
                            selected = kategori == option,
                            onClick = { kategori = option }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = isi,
                onValueChange = { isi = it },
                label = { Text("Isi konten") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                minLines = 4
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEach { option ->
                        OptionChip(
                            text = option,
                            selected = status == option,
                            onClick = { status = option }
                        )
                    }
                }
            }

            Text(
                text = if (kategori == "Program") {
                    "Konten Program berstatus Aktif akan tampil di preview Personalisasi Program pengguna."
                } else {
                    "Konten Aktif akan tampil di halaman user sesuai kategori. Konten Nonaktif disembunyikan."
                },
                color = Color(0xFF6B7280),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Batal")
                }

                Button(
                    enabled = judul.isNotBlank() && isi.isNotBlank() && !isSaving,
                    onClick = {
                        scope.launch {
                            isSaving = true
                            val konten = KontenEntity(
                                idKonten = idKonten,
                                judul = judul.trim(),
                                kategori = kategori,
                                isi = isi.trim(),
                                status = status
                            )
                            if (idKonten == 0L) {
                                dao.insertKonten(konten.copy(idKonten = 0L))
                            } else {
                                dao.updateKonten(konten)
                            }
                            isSaving = false
                            onSaved()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        text = if (isSaving) "Menyimpan..." else "Simpan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text, fontWeight = FontWeight.Bold) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrimaryBlue,
            selectedLabelColor = Color.White,
            containerColor = Color(0xFFE8EEF7),
            labelColor = Color(0xFF4B5563)
        ),
        border = null
    )
}
