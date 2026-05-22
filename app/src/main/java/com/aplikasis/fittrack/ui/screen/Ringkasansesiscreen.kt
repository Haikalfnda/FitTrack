package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.ui.theme.*

// ─── Data dummy ───────────────────────────────────────────────────────────────

private data class RingkasanSesiData(
    val totalReps: Int,
    val durasi: String,
    val kaloriTerbakar: Int,
    val targetHarian: Boolean,
    val streakStatus: Boolean
)

private val dummyRingkasan = RingkasanSesiData(
    totalReps = 95,
    durasi = "26 mnt",
    kaloriTerbakar = 120,
    targetHarian = true,
    streakStatus = true
)

private val yangTersimpan = listOf(
    "Tanggal & jenis latihan",
    "Repetisi, set, dan durasi",
    "Estimasi kalori terbakar",
    "Status target harian & streak"
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun RingkasanSesiScreen(
    onLihatRiwayat: () -> Unit = {},
    onBukaProgressTracking: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val data = dummyRingkasan

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // ── Banner Selesai ────────────────────────────────────────────────────
        BannerSelesai()

        Spacer(modifier = Modifier.height(20.dp))

        // ── Stat Row ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MiniStatCard(label = "Reps", value = "${data.totalReps}", modifier = Modifier.weight(1f))
            MiniStatCard(label = "Durasi", value = data.durasi, modifier = Modifier.weight(1f))
            MiniStatCard(label = "Kalori", value = "${data.kaloriTerbakar}", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Yang Tersimpan ────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Apa yang tersimpan?",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                yangTersimpan.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall.copy(color = DarkText)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Lihat Riwayat ─────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Lihat riwayat latihan",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Lihat sesi latihan dan catatan\nlatihan sebelumnya.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MutedText,
                            lineHeight = 17.sp
                        )
                    )
                }
                TextButton(onClick = onLihatRiwayat) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ── Cek Progress ──────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Lanjut cek progress",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "User bisa langsung melihat\ntren mingguan, kenaikan.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MutedText,
                            lineHeight = 17.sp
                        )
                    )
                }
                TextButton(onClick = onBukaProgressTracking) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Tombol Progress Tracking ──────────────────────────────────────────
        Button(
            onClick = onBukaProgressTracking,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(
                text = "Buka progress tracking",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ─── Banner Selesai ───────────────────────────────────────────────────────────

@Composable
private fun BannerSelesai() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2F66EB), Color(0xFF6D79FF))
                )
            )
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ringkasan sesi hari ini",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 17.sp
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎉", fontSize = 30.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sesi selesai & tersimpan",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ringkasan sesi hari ini 🎉\nSemua exercise berhasil dicatat otomatis.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

// ─── Mini Stat Card ───────────────────────────────────────────────────────────

@Composable
private fun MiniStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontSize = 18.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MutedText,
                    fontSize = 11.sp
                )
            )
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFD)
@Composable
fun RingkasanSesiScreenPreview() {
    FitrackTheme {
        RingkasanSesiScreen()
    }
}