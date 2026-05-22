package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.ui.theme.*

// ─── Data dummy ───────────────────────────────────────────────────────────────

private enum class StatusGerakan { SEDANG, SELANJUTNYA }

private data class GerakanData(
    val status: StatusGerakan,
    val nama: String,
    val estimasiKalori: Int,
    val set: Int,
    val repetisi: Int,
    val istirahat: String? = null,   // null kalau tidak ada
    val catatan: String? = null      // target / transisi
)

private data class DetailProgramData(
    val judulProgram: String,
    val deskripsi: String,
    val minggu: Int,
    val level: String,
    val daftarGerakan: List<GerakanData>
)

private val dummyDetail = DetailProgramData(
    judulProgram = "Upper Body",
    deskripsi = "Fokus form, repetisi stabil, dan progression aman.",
    minggu = 1,
    level = "Level Beginner",
    daftarGerakan = listOf(
        GerakanData(
            status = StatusGerakan.SEDANG,
            nama = "Push Up",
            estimasiKalori = 120,
            set = 3,
            repetisi = 12,
            istirahat = "Rest 45 detik",
            catatan = "Jaga siku 45° dan posisi tubuh tetap lurus."
        ),
        GerakanData(
            status = StatusGerakan.SELANJUTNYA,
            nama = "Shoulder Tap",
            estimasiKalori = 20,
            set = 3,
            repetisi = 10,
            istirahat = null,
            catatan = "Transisi berikutnya untuk menjaga form tetap rapi."
        ),
        GerakanData(
            status = StatusGerakan.SELANJUTNYA,
            nama = "Plank",
            estimasiKalori = 20,
            set = 1,
            repetisi = 45,  // detik
            istirahat = null,
            catatan = null
        )
    )
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun DetailProgramScreen(
    onBackClick: () -> Unit = {},
    onSelesaiSesi: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val data = dummyDetail
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        // ── Top Bar ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = DarkText
                )
            }
            Text(
                text = "Detail Program",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        HorizontalDivider(color = BorderColor)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Header Program ───────────────────────────────────────────────
            HeaderDetailCard(data = data)

            Spacer(modifier = Modifier.height(16.dp))

            // ── Daftar Gerakan ───────────────────────────────────────────────
            data.daftarGerakan.forEach { gerakan ->
                GerakanCard(gerakan = gerakan)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Tombol Selesai (fixed di bawah) ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = onSelesaiSesi,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                Text(
                    text = "Selesai Sesi Ini",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                )
            }
        }
    }
}

// ─── Header Detail Card ───────────────────────────────────────────────────────

@Composable
private fun HeaderDetailCard(data: DetailProgramData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2F66EB), Color(0xFF6D79FF))
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "PROGRAM LATIHAN HARI INI",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = data.judulProgram,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 28.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = data.deskripsi,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipHeaderWhite(text = "Minggu ${data.minggu}")
                ChipHeaderWhite(text = data.level)
            }
        }
    }
}

// ─── Gerakan Card ─────────────────────────────────────────────────────────────

@Composable
private fun GerakanCard(gerakan: GerakanData) {
    val isSedang = gerakan.status == StatusGerakan.SEDANG

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .then(
                if (isSedang) Modifier.border(
                    width = 1.5.dp,
                    color = PrimaryBlue,
                    shape = RoundedCornerShape(14.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSedang) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Label status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isSedang) "GERAKAN YANG DILAKUKAN" else "GERAKAN SELANJUTNYA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MutedText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    if (isSedang) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF22C55E))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Sedang dikerjakan",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Next Move",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MutedText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Nama gerakan
                Text(
                    text = gerakan.nama,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Estimasi kalori
                Text(
                    text = "Estimasi Calorie Terbakar : ${gerakan.estimasiKalori}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MutedText,
                        fontSize = 11.sp
                    )
                )

                // Set & repetisi
                val repetisiLabel = if (gerakan.nama == "Plank") "${gerakan.repetisi} Detik"
                else "${gerakan.set} set × ${gerakan.repetisi} repetisi"
                val infoLengkap = if (gerakan.istirahat != null) "$repetisiLabel • ${gerakan.istirahat}"
                else repetisiLabel
                Text(
                    text = infoLengkap,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MutedText,
                        fontSize = 11.sp
                    )
                )

                // Catatan / target
                gerakan.catatan?.let { catatan ->
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isSedang) {
                        Text(
                            text = "Target",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MutedText,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Text(
                        text = catatan,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DarkText,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Tombol play
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Mulai gerakan",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ─── Helper ───────────────────────────────────────────────────────────────────

@Composable
private fun ChipHeaderWhite(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFD)
@Composable
fun DetailProgramScreenPreview() {
    FitrackTheme {
        DetailProgramScreen()
    }
}