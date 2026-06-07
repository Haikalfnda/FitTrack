package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.model.DetailGerakan
import com.aplikasis.fittrack.model.GerakanWorkout
import com.aplikasis.fittrack.model.HasilSesi
import com.aplikasis.fittrack.model.KaloriPerRep
import com.aplikasis.fittrack.ui.theme.*

// ─── Data Sesi ────────────────────────────────────────────────────────────────

/**
 * FITUR 5: Daftar gerakan sesi dengan kaloriPerRep dari KaloriPerRep object.
 * repTarget = set × repetisi per set.
 */
private val sesiGerakan = listOf(
    GerakanWorkout(
        nama = "Push Up",
        kaloriPerRep = KaloriPerRep.PUSH_UP,
        set = 3,
        repTarget = 3 * 12,   // 36 total
        istirahat = "Rest 45 detik",
        catatan = "Jaga siku 45° dan posisi tubuh tetap lurus.",
        isSedang = true
    ),
    GerakanWorkout(
        nama = "Shoulder Tap",
        kaloriPerRep = KaloriPerRep.SHOULDER_TAP,
        set = 3,
        repTarget = 3 * 10,   // 30 total
        catatan = "Transisi berikutnya untuk menjaga form tetap rapi."
    ),
    GerakanWorkout(
        nama = "Plank",
        kaloriPerRep = KaloriPerRep.PLANK,
        set = 1,
        repTarget = 45         // detik
    )
)

// ─── Screen ───────────────────────────────────────────────────────────────────

/**
 * FITUR 5 - Perubahan DetailProgramScreen:
 *
 * 1. Setiap gerakan memiliki input field "Repetisi Selesai".
 * 2. Kalori dihitung real-time: kaloriPerRep × repSelesai.
 * 3. Total kalori & total reps ditampilkan di header sesi.
 * 4. Saat "Selesai Sesi Ini" → data (totalRep, totalKalori, detailGerakan)
 *    diteruskan via [onSelesaiSesi] ke NavGraph untuk disimpan ke Room.
 *
 * Kompatibilitas:
 * - [onSelesaiSesi] sekarang membawa [HasilSesi] (totalRep + totalKalori + detail).
 * - NavGraph diperbarui untuk meneruskan data ini ke RingkasanSesiScreen.
 */
@Composable
fun DetailProgramScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSelesaiSesi: (HasilSesi) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // State repetisi selesai per gerakan — index sesuai sesiGerakan
    val repSelesaiList = remember {
        mutableStateListOf(*Array(sesiGerakan.size) { 0 })
    }

    // Hitung total kalori & reps secara real-time
    val totalKalori by remember {
        derivedStateOf {
            sesiGerakan.indices.sumOf { i ->
                sesiGerakan[i].kaloriPerRep * repSelesaiList[i]
            }
        }
    }
    val totalRep by remember {
        derivedStateOf { repSelesaiList.sum() }
    }

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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
            HeaderDetailCard(
                totalRep = totalRep,
                totalKalori = totalKalori
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Daftar Gerakan ───────────────────────────────────────────────
            sesiGerakan.forEachIndexed { index, gerakan ->
                GerakanCard(
                    gerakan = gerakan,
                    repSelesai = repSelesaiList[index],
                    onRepSelesaiChange = { baru ->
                        // Kunci agar tidak melebihi target
                        repSelesaiList[index] = baru.coerceIn(0, gerakan.repTarget)
                    }
                )
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
                onClick = {
                    val detail = sesiGerakan.mapIndexed { i, g ->
                        DetailGerakan(
                            nama = g.nama,
                            repSelesai = repSelesaiList[i],
                            repTarget = g.repTarget,
                            kalori = g.kaloriPerRep * repSelesaiList[i]
                        )
                    }
                    onSelesaiSesi(
                        HasilSesi(
                            totalRep = totalRep,
                            totalKalori = totalKalori,
                            detailGerakan = detail
                        )
                    )
                },
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
private fun HeaderDetailCard(
    totalRep: Int,
    totalKalori: Double
) {
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
                text = "Upper Body",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 28.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fokus form, repetisi stabil, dan progression aman.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White.copy(alpha = 0.8f)
                )
            )
            Spacer(modifier = Modifier.height(14.dp))

            // FITUR 5: Tampilkan total real-time di header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeaderStatChip(
                    label = "Total Reps",
                    value = "$totalRep",
                    modifier = Modifier.weight(1f)
                )
                HeaderStatChip(
                    label = "Kalori",
                    value = "%.1f kcal".format(totalKalori),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipHeaderWhite(text = "Minggu 1")
                ChipHeaderWhite(text = "Level Beginner")
            }
        }
    }
}

@Composable
private fun HeaderStatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            )
        }
    }
}

// ─── Gerakan Card ─────────────────────────────────────────────────────────────

/**
 * FITUR 5: GerakanCard sekarang memiliki:
 * - Input field jumlah repetisi yang benar-benar diselesaikan
 * - Tombol +/- untuk kemudahan input
 * - Tampilan kalori real-time berdasarkan repSelesai
 */
@Composable
private fun GerakanCard(
    gerakan: GerakanWorkout,
    repSelesai: Int,
    onRepSelesaiChange: (Int) -> Unit
) {
    val kaloriAktual = gerakan.kaloriPerRep * repSelesai
    val progressRep = if (gerakan.repTarget > 0) repSelesai.toFloat() / gerakan.repTarget else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .then(
                if (gerakan.isSedang) Modifier.border(
                    width = 1.5.dp,
                    color = PrimaryBlue,
                    shape = RoundedCornerShape(14.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (gerakan.isSedang) 3.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Label status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (gerakan.isSedang) "GERAKAN YANG DILAKUKAN" else "GERAKAN SELANJUTNYA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MutedText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (gerakan.isSedang) Color(0xFF22C55E) else Color(0xFFF3F4F6)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (gerakan.isSedang) "Sedang dikerjakan" else "Next Move",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (gerakan.isSedang) Color.White else MutedText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            )
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

                    // Set & target
                    val isPlank = gerakan.nama.contains("plank", ignoreCase = true)
                    val targetLabel = if (isPlank)
                        "${gerakan.repTarget} Detik"
                    else
                        "${gerakan.set} set × ${gerakan.repTarget / gerakan.set} repetisi"

                    val infoLengkap = if (gerakan.istirahat != null)
                        "$targetLabel • ${gerakan.istirahat}"
                    else targetLabel

                    Text(
                        text = infoLengkap,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    )

                    // FITUR 5: Kalori real-time
                    Text(
                        text = "🔥 %.2f kcal (%.2f kcal/${if (isPlank) "detik" else "rep"})".format(
                            kaloriAktual, gerakan.kaloriPerRep
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFEF4444),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    gerakan.catatan?.let { catatan ->
                        Spacer(modifier = Modifier.height(6.dp))
                        if (gerakan.isSedang) {
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

                // Icon play
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

            // FITUR 5: Input repetisi selesai + progress bar
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Repetisi selesai",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Kontrol +/-
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol minus
                IconButton(
                    onClick = { onRepSelesaiChange(repSelesai - 1) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BorderColor)
                ) {
                    Text(
                        text = "−",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        textAlign = TextAlign.Center
                    )
                }

                // Input angka langsung
                OutlinedTextField(
                    value = if (repSelesai == 0) "" else repSelesai.toString(),
                    onValueChange = { input ->
                        val angka = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                        onRepSelesaiChange(angka)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    placeholder = {
                        Text(
                            text = "0",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = MutedText
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = BorderColor
                    )
                )

                // Tombol plus
                IconButton(
                    onClick = { onRepSelesaiChange(repSelesai + 1) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                ) {
                    Text(
                        text = "+",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                // Label target
                Text(
                    text = "/ ${gerakan.repTarget}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MutedText,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar repetisi
            LinearProgressIndicator(
                progress = { progressRep.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when {
                    progressRep >= 1f -> Color(0xFF22C55E)
                    progressRep >= 0.5f -> PrimaryBlue
                    else -> Color(0xFFF59E0B)
                },
                trackColor = BorderColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$repSelesai / ${gerakan.repTarget} rep${if (progressRep >= 1f) " ✓" else ""}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (progressRep >= 1f) Color(0xFF22C55E) else MutedText,
                    fontSize = 10.sp
                )
            )
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