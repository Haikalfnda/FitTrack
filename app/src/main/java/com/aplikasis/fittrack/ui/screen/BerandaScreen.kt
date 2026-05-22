package com.aplikasis.fittrack.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.ui.theme.*

// ─── Data dummy (nanti ganti dari ViewModel/DB) ───────────────────────────────

private data class ProgramAktif(
    val nama: String,
    val frekuensi: String,
    val tipe: String,
    val progressMinggu: Int,       // persen 0-100
    val mingguSaatIni: Int,
    val totalMinggu: Int,
    val hariTarget: Int,
    val hariSelesai: Int,
    val level: String,
    val tujuan: String,
    val nextProgression: String
)

private data class RingkasanCepat(
    val totalReps: Int,
    val kalori: Int,
    val durasiJam: Double
)

private val dummyProgram = ProgramAktif(
    nama = "Full Body Beginner",
    frekuensi = "4 minggu • 2x per minggu • Personal",
    tipe = "Hari ini: Push up + Squat + Plank",
    progressMinggu = 72,
    mingguSaatIni = 3,
    totalMinggu = 4,
    hariTarget = 4,
    hariSelesai = 3,
    level = "Level Pemula",
    tujuan = "Goal: Turun berat badan",
    nextProgression = "+2 reps"
)

private val dummyRingkasan = RingkasanCepat(
    totalReps = 1280,
    kalori = 3420,
    durasiJam = 18.4
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun BerandaScreen(
    user: UserEntity,
    streakHari: Int = 12,          // nanti dari DB / ViewModel
    onLanjutLatihan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(scrollState)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        HeaderCard(
            namaUser = user.nama,
            streakHari = streakHari,
            hariSelesai = dummyProgram.hariSelesai,
            hariTarget = dummyProgram.hariTarget,
            progressMinggu = dummyProgram.progressMinggu
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Program Aktif ────────────────────────────────────────────────────
        Text(
            text = "Program Aktif",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkText,
                fontSize = 17.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProgramAktifCard(
            program = dummyProgram,
            onLanjutLatihan = onLanjutLatihan
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Ringkasan Cepat ──────────────────────────────────────────────────
        Text(
            text = "Ringkasan Cepat",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkText,
                fontSize = 17.sp
            ),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        RingkasanCepatRow(ringkasan = dummyRingkasan)

        Spacer(modifier = Modifier.height(28.dp))
    }
}

// ─── Header Card ─────────────────────────────────────────────────────────────

@Composable
private fun HeaderCard(
    namaUser: String,
    streakHari: Int,
    hariSelesai: Int,
    hariTarget: Int,
    progressMinggu: Int
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )
    LaunchedEffect(Unit) { animatedProgress = progressMinggu / 100f }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2F66EB), Color(0xFF6D79FF))
                )
            )
            .padding(20.dp)
    ) {
        Column {
            // Salam + streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Halo, ${namaUser.split(" ").first()} 👋",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )
                    Text(
                        text = "Latihan hari ini",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Program terstruktur dengan\nprogression sesuai level\nkemampuanmu.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.75f),
                            lineHeight = 18.sp
                        )
                    )
                }

                // Streak badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🔥",
                                fontSize = 18.sp
                            )
                            Text(
                                text = "$streakHari",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Streak $streakHari hari",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar + target
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Target $hariSelesai/$hariTarget",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "${progressMinggu}%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "minggu 2",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            )
        }
    }
}

// ─── Program Aktif Card ───────────────────────────────────────────────────────

@Composable
private fun ProgramAktifCard(
    program: ProgramAktif,
    onLanjutLatihan: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Nama program
            Text(
                text = program.nama,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            )
            Text(
                text = program.frekuensi,
                style = MaterialTheme.typography.bodySmall.copy(color = MutedText),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress minggu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress minggu ini",
                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                )
                Text(
                    text = "${program.progressMinggu}%",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { program.progressMinggu / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryBlue,
                trackColor = BorderColor,
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Progression minggu depan otomatis naik jika target tercapai.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MutedText,
                    fontSize = 10.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(12.dp))

            // Hari ini
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE8F0FD))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Hari ini",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = program.tipe,
                    style = MaterialTheme.typography.bodySmall.copy(color = DarkText),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Level & Tujuan
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipLabel(text = program.level, color = Color(0xFF6D79FF))
                ChipLabel(text = program.tujuan, color = Color(0xFF24C6C2))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Next progression + Lanjut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next progression: ${program.nextProgression}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MutedText,
                        fontSize = 11.sp
                    )
                )
                Button(
                    onClick = onLanjutLatihan,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Lanjut latihan",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

// ─── Ringkasan Cepat ──────────────────────────────────────────────────────────

@Composable
private fun RingkasanCepatRow(ringkasan: RingkasanCepat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Default.FitnessCenter,
            iconColor = PrimaryBlue,
            label = "Total Reps",
            value = "%,d".format(ringkasan.totalReps),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.Whatshot,
            iconColor = Color(0xFFFF6B35),
            label = "Kalori",
            value = "%,d".format(ringkasan.kalori),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.Timer,
            iconColor = Color(0xFF24C6C2),
            label = "Durasi",
            value = "${ringkasan.durasiJam} jam",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontSize = 15.sp
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MutedText,
                    fontSize = 10.sp
                )
            )
        }
    }
}

// ─── Helper Composables ───────────────────────────────────────────────────────

@Composable
private fun ChipLabel(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFD)
@Composable
fun BerandaScreenPreview() {
    FitrackTheme {
        BerandaScreen(
            user = UserEntity(
                idUser = 1,
                nama = "Fabio Santoso",
                email = "fabio@email.com",
                password = "",
                level = "Pemula",
                tujuan = "Turun berat badan"
            ),
            streakHari = 12
        )
    }
}