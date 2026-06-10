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
import com.aplikasis.fittrack.model.DetailGerakan
import com.aplikasis.fittrack.model.HasilSesi
import com.aplikasis.fittrack.ui.theme.*

private val yangTersimpan = listOf(
    "Tanggal & jenis latihan",
    "Repetisi, set, dan durasi",
    "Estimasi kalori terbakar",
    "Status target harian & streak"
)


@Composable
fun RingkasanSesiScreen(
    hasilSesi: HasilSesi? = null,
    onLihatRiwayat: () -> Unit = {},
    onBukaProgressTracking: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        BannerSelesai()

        Spacer(modifier = Modifier.height(20.dp))

        // ── Stat Row ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MiniStatCard(
                label = "Reps",
                value = "${hasilSesi?.totalRep ?: 0}",
                modifier = Modifier.weight(1f)
            )
            MiniStatCard(
                label = "Durasi",
                value = hasilSesi?.durasiLabel ?: "0 detik",
                modifier = Modifier.weight(1f)
            )
            MiniStatCard(
                label = "Kalori",
                value = "%.1f".format(hasilSesi?.totalKalori ?: 0.0),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── FITUR 5: Detail Per Gerakan ───────────────────────────────────────
        if (hasilSesi != null && hasilSesi.detailGerakan.isNotEmpty()) {
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
                        text = "Workout Summary",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            fontSize = 15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    hasilSesi.detailGerakan.forEach { detail ->
                        GerakanRingkasanRow(detail = detail)
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    HorizontalDivider(color = BorderColor)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Total baris
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${hasilSesi.totalRep} reps - ${hasilSesi.durasiLabel}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                            )
                            Text(
                                text = "%.2f kcal".format(hasilSesi.totalKalori),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

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
                            fontWeight = FontWeight.Bold, color = DarkText
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Lihat sesi latihan dan catatan\nlatihan sebelumnya.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MutedText, lineHeight = 17.sp
                        )
                    )
                }
                TextButton(onClick = onLihatRiwayat) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PrimaryBlue, fontWeight = FontWeight.Bold
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
                            fontWeight = FontWeight.Bold, color = DarkText
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "User bisa langsung melihat\ntren mingguan, kenaikan.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MutedText, lineHeight = 17.sp
                        )
                    )
                }
                TextButton(onClick = onBukaProgressTracking) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = PrimaryBlue, fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                    fontWeight = FontWeight.Bold, color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ─── Gerakan Ringkasan Row ────────────────────────────────────────────────────

@Composable
private fun GerakanRingkasanRow(detail: DetailGerakan) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detail.nama,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
            )
            Text(
                text = detail.progressLabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MutedText,
                    fontSize = 11.sp
                )
            )
            if (detail.setLabel.isNotBlank()) {
                Text(
                    text = detail.setLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MutedText,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = detail.kaloriLabel,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            )
            // Badge selesai/tidak
            if (detail.repSelesai >= detail.repTarget) {
                Text(
                    text = "Target",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF22C55E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
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
        RingkasanSesiScreen(
            hasilSesi = HasilSesi(
                totalRep = 47,
                totalKalori = 14.95,
                detailGerakan = listOf(
                    DetailGerakan("Push Up", 12, 36, 4.8),
                    DetailGerakan("Shoulder Tap", 20, 30, 3.6),
                    DetailGerakan("Plank", 15, 45, 1.5)
                )
            )
        )
    }
}
