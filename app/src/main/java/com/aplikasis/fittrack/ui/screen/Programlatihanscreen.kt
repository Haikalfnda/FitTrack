package com.aplikasis.fittrack.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.ui.theme.*

// ─── Data dummy ───────────────────────────────────────────────────────────────

private data class HariLatihan(
    val nomor: Int,
    val judul: String,
    val gerakan: String,
    val status: StatusHari  // Selesai, HariIni, Terjadwal
)

private enum class StatusHari { SELESAI, HARI_INI, TERJADWAL }

private data class ProgramLatihanData(
    val nama: String,
    val mingguSaatIni: Int,
    val totalMinggu: Int,
    val fokus: String,
    val progressPersen: Int,
    val progressionMadness: Int,  // persen
    val level: String,
    val daftarHari: List<HariLatihan>
)

private val dummyProgramLatihan = ProgramLatihanData(
    nama = "Full Body Beginner",
    mingguSaatIni = 3,
    totalMinggu = 4,
    fokus = "Konsistensi + belum berharga",
    progressPersen = 80,
    progressionMadness = 80,
    level = "Level Beginner",
    daftarHari = listOf(
        HariLatihan(
            nomor = 1,
            judul = "Upper Body",
            gerakan = "Push up 3x12 • Shoulder tap 10x3 • Plank 45 dtk",
            status = StatusHari.SELESAI
        ),
        HariLatihan(
            nomor = 2,
            judul = "Lower Body",
            gerakan = "Squat 10x8 • Lunge 10x8 • Wall sit 40 dtk",
            status = StatusHari.SELESAI
        ),
        HariLatihan(
            nomor = 3,
            judul = "Full Body",
            gerakan = "Burpee 8x3 • Mountain climber 20x3 • Jumping jack",
            status = StatusHari.HARI_INI
        ),
        HariLatihan(
            nomor = 4,
            judul = "Core & Cardio",
            gerakan = "Sit up 15x3 • Plank 60 dtk • High knees 30 dtk",
            status = StatusHari.TERJADWAL
        )
    )
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun ProgramLatihanScreen(
    onBackClick: () -> Unit = {},
    onMulaiSesi: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val program = dummyProgramLatihan
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
                text = "Program Latihan",
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
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // ── Header Info Program ──────────────────────────────────────────
            HeaderProgramCard(program = program)

            Spacer(modifier = Modifier.height(20.dp))

            // ── Daftar Hari ──────────────────────────────────────────────────
            Text(
                text = "Minggu ${program.mingguSaatIni} / ${program.totalMinggu}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Fokus: ${program.fokus}",
                style = MaterialTheme.typography.bodySmall.copy(color = MutedText),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progression madness bar
            ProgressionBar(
                label = "Progression madness",
                persen = program.progressionMadness,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Progression minggu depan otomatis naik jika target tercapai.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MutedText,
                    fontSize = 10.sp
                ),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Daftar hari
            program.daftarHari.forEach { hari ->
                HariCard(hari = hari)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Tombol Mulai ─────────────────────────────────────────────────
            Button(
                onClick = onMulaiSesi,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Mulai sesi hari ini",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Header Program Card ──────────────────────────────────────────────────────

@Composable
private fun HeaderProgramCard(program: ProgramLatihanData) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(800),
        label = "progress"
    )
    LaunchedEffect(Unit) { animatedProgress = program.progressPersen / 100f }

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
            Text(
                text = program.nama,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "Structured program dengan sistem progression bertahap.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Minggu ${program.mingguSaatIni} / ${program.totalMinggu}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.85f)
                    )
                )
                Text(
                    text = "+2",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFFB0FFC8),
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
            Spacer(modifier = Modifier.height(8.dp))

            // Badge level
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipWhite(text = program.level)
            }
        }
    }
}

// ─── Hari Card ────────────────────────────────────────────────────────────────

@Composable
private fun HariCard(hari: HariLatihan) {
    val isHariIni = hari.status == StatusHari.HARI_INI
    val isSelesai = hari.status == StatusHari.SELESAI
    val isTerjadwal = hari.status == StatusHari.TERJADWAL

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .then(
                if (isHariIni) Modifier.border(
                    width = 1.5.dp,
                    color = PrimaryBlue,
                    shape = RoundedCornerShape(14.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHariIni) Color(0xFFF0F5FF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHariIni) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nomor / status icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isSelesai -> Color(0xFF22C55E)
                            isHariIni -> PrimaryBlue
                            else -> BorderColor
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isSelesai -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    isTerjadwal -> Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MutedText,
                        modifier = Modifier.size(16.dp)
                    )
                    else -> Text(
                        text = "${hari.nomor}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Info hari
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Hari ${hari.nomor} • ${hari.judul}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isTerjadwal) MutedText else DarkText
                        )
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = hari.gerakan,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MutedText,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Badge status
            when {
                isHariIni -> Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PrimaryBlue)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Hari ini",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
                isSelesai -> Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Selesai",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
                else -> Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(BorderColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Terjadwal",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MutedText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

// ─── Progression Bar ──────────────────────────────────────────────────────────

@Composable
private fun ProgressionBar(
    label: String,
    persen: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
            )
            Text(
                text = "$persen%",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { persen / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = PrimaryBlue,
            trackColor = BorderColor,
            strokeCap = StrokeCap.Round
        )
    }
}

// ─── Helper ───────────────────────────────────────────────────────────────────

@Composable
private fun ChipWhite(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
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
fun ProgramLatihanScreenPreview() {
    FitrackTheme {
        ProgramLatihanScreen()
    }
}