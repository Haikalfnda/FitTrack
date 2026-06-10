package com.aplikasis.fittrack.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.data.entity.KontenEntity
import com.aplikasis.fittrack.model.DetailGerakan
import com.aplikasis.fittrack.model.GerakanWorkout
import com.aplikasis.fittrack.model.HasilSesi
import com.aplikasis.fittrack.model.KaloriPerRep
import com.aplikasis.fittrack.model.TipeGerakan
import com.aplikasis.fittrack.ui.theme.BorderColor
import com.aplikasis.fittrack.ui.theme.DarkText
import com.aplikasis.fittrack.ui.theme.FitrackTheme
import com.aplikasis.fittrack.ui.theme.MutedText
import com.aplikasis.fittrack.ui.theme.PrimaryBlue
import com.aplikasis.fittrack.ui.theme.ScreenBg
import kotlinx.coroutines.delay

private enum class StatusSesi {
    BELUM_MULAI,
    HITUNG_MUNDUR,
    BERJALAN,
    SELESAI
}

private val sesiGerakan = listOf(
    GerakanWorkout(
        nama = "Push Up",
        kaloriPerRep = KaloriPerRep.PUSH_UP,
        set = 3,
        repTarget = 36,
        istirahat = "Rest 45 detik",
        catatan = "Jaga siku 45 derajat dan tubuh tetap lurus.",
        tipe = TipeGerakan.REPS
    ),
    GerakanWorkout(
        nama = "Shoulder Tap",
        kaloriPerRep = KaloriPerRep.SHOULDER_TAP,
        set = 3,
        repTarget = 30,
        istirahat = "Rest 40 detik",
        catatan = "Pinggul stabil, bahu tetap sejajar.",
        tipe = TipeGerakan.REPS
    ),
    GerakanWorkout(
        nama = "Plank",
        kaloriPerRep = KaloriPerRep.PLANK,
        set = 1,
        repTarget = 45,
        istirahat = null,
        catatan = "Tahan posisi core, jangan turunkan pinggul.",
        tipe = TipeGerakan.DURASI
    )
)

@Composable
fun DetailProgramScreen(
    modifier: Modifier = Modifier,
    namaProgram: String = "Full Body Beginner",
    hariLatihan: String = "Sesi hari ini",
    fokusLatihan: String = "Full body dan core",
    levelLatihan: String = "Pemula",
    arahTargetBerat: String = "",
    tipsSebelumLatihan: KontenEntity? = null,
    onBackClick: () -> Unit = {},
    onPlayTutorial: (String) -> Unit = {},
    onSelesaiSesi: (HasilSesi) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val daftarGerakan = remember(fokusLatihan, arahTargetBerat) {
        pilihGerakanSesi(fokusLatihan, arahTargetBerat)
    }
    var statusSesi by rememberSaveable { mutableStateOf(StatusSesi.BELUM_MULAI) }
    var countdown by rememberSaveable { mutableIntStateOf(10) }

    val inputPerSet = remember(daftarGerakan) {
        daftarGerakan.map { gerakan ->
            mutableStateListOf(*Array(gerakan.set.coerceAtLeast(1)) { "" })
        }
    }

    LaunchedEffect(statusSesi) {
        if (statusSesi == StatusSesi.HITUNG_MUNDUR) {
            for (value in 10 downTo 1) {
                countdown = value
                delay(1000)
            }
            statusSesi = StatusSesi.BERJALAN
        }
    }

    val totalAktualPerGerakan by remember(daftarGerakan, inputPerSet) {
        derivedStateOf {
            daftarGerakan.indices.map { index ->
                inputPerSet[index].sumOf { it.toIntOrNull() ?: 0 }
            }
        }
    }
    val totalRepAktual by remember(daftarGerakan, totalAktualPerGerakan) {
        derivedStateOf {
            daftarGerakan.indices.sumOf { index ->
                if (daftarGerakan[index].tipe == TipeGerakan.REPS) totalAktualPerGerakan[index] else 0
            }
        }
    }
    val totalDurasiAktual by remember(daftarGerakan, totalAktualPerGerakan) {
        derivedStateOf {
            daftarGerakan.indices.sumOf { index ->
                if (daftarGerakan[index].tipe == TipeGerakan.DURASI) totalAktualPerGerakan[index] else 0
            }
        }
    }
    val totalKaloriAktual by remember(daftarGerakan, totalAktualPerGerakan) {
        derivedStateOf {
            daftarGerakan.indices.sumOf { index ->
                totalAktualPerGerakan[index] * daftarGerakan[index].kaloriPerRep
            }
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            DetailTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            SesiBottomBar(
                statusSesi = statusSesi,
                onFinish = {
                    statusSesi = StatusSesi.SELESAI
                    onSelesaiSesi(
                        HasilSesi(
                            namaProgram = namaProgram,
                            hariLatihan = hariLatihan,
                            fokusLatihan = fokusLatihan,
                            level = levelLatihan,
                            totalRep = totalRepAktual,
                            totalDurasiDetik = totalDurasiAktual,
                            totalKalori = totalKaloriAktual,
                            detailGerakan = daftarGerakan.mapIndexed { index, gerakan ->
                                val nilaiSet = inputPerSet[index].map { it.toIntOrNull() ?: 0 }
                                val totalAktual = nilaiSet.sum()
                                DetailGerakan(
                                    nama = gerakan.nama,
                                    repSelesai = totalAktual,
                                    repTarget = gerakan.repTarget,
                                    kalori = totalAktual * gerakan.kaloriPerRep,
                                    setSelesai = nilaiSet,
                                    targetPerSet = gerakan.targetPerSet,
                                    jumlahSet = gerakan.set,
                                    tipe = gerakan.tipe
                                )
                            }
                        )
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SessionHeaderCard(
                namaProgram = namaProgram,
                hariLatihan = hariLatihan,
                fokusLatihan = fokusLatihan,
                levelLatihan = levelLatihan,
                statusSesi = statusSesi,
                totalRep = totalRepAktual,
                totalDurasiDetik = totalDurasiAktual,
                totalKalori = totalKaloriAktual
            )

            Spacer(modifier = Modifier.height(14.dp))

            when (statusSesi) {
                StatusSesi.BELUM_MULAI -> {
                    RencanaLatihanCard(daftarGerakan = daftarGerakan)
                    Spacer(modifier = Modifier.height(14.dp))
                    TipsSebelumLatihanCard(tips = tipsSebelumLatihan)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { statusSesi = StatusSesi.HITUNG_MUNDUR },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mulai Latihan",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                StatusSesi.HITUNG_MUNDUR -> {
                    CountdownCard(countdown = countdown)
                    Spacer(modifier = Modifier.height(14.dp))
                    TipsSebelumLatihanCard(tips = tipsSebelumLatihan)
                    Spacer(modifier = Modifier.height(14.dp))
                    RencanaLatihanCard(daftarGerakan = daftarGerakan)
                }

                StatusSesi.BERJALAN,
                StatusSesi.SELESAI -> {
                    Text(
                        text = "Input hasil aktual per set",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = DarkText,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    daftarGerakan.forEachIndexed { index, gerakan ->
                        GerakanSetInputCard(
                            gerakan = gerakan,
                            nilaiSet = inputPerSet[index],
                            totalAktual = totalAktualPerGerakan[index],
                            enabled = statusSesi == StatusSesi.BERJALAN,
                            onPlayTutorial = onPlayTutorial,
                            onSetValueChange = { setIndex, value ->
                                inputPerSet[index][setIndex] = value.filter { it.isDigit() }.take(4)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun DetailTopBar(onBackClick: () -> Unit) {
    Column {
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
    }
}

@Composable
private fun SessionHeaderCard(
    namaProgram: String,
    hariLatihan: String,
    fokusLatihan: String,
    levelLatihan: String,
    statusSesi: StatusSesi,
    totalRep: Int,
    totalDurasiDetik: Int,
    totalKalori: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF2F66EB), Color(0xFF6D79FF))
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = namaProgram,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$hariLatihan - $fokusLatihan",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.82f),
                                lineHeight = 18.sp
                            )
                        )
                    }
                    StatusBadge(statusSesi = statusSesi)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HeaderChip("Level $levelLatihan")
                    HeaderChip(if (statusSesi == StatusSesi.BERJALAN) "Sesi berjalan" else "Siap mulai")
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HeaderStat("Reps", "$totalRep", Modifier.weight(1f))
                    HeaderStat("Durasi", formatDurasiDetik(totalDurasiDetik), Modifier.weight(1f))
                    HeaderStat("Kalori", "%.1f".format(totalKalori), Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(statusSesi: StatusSesi) {
    val label = when (statusSesi) {
        StatusSesi.BELUM_MULAI -> "Belum mulai"
        StatusSesi.HITUNG_MUNDUR -> "Countdown"
        StatusSesi.BERJALAN -> "Berjalan"
        StatusSesi.SELESAI -> "Selesai"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun HeaderChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HeaderStat(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1
            )
            Text(text = label, color = Color.White.copy(alpha = 0.78f), fontSize = 10.sp)
        }
    }
}

@Composable
private fun RencanaLatihanCard(daftarGerakan: List<GerakanWorkout>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Rencana latihan",
                color = DarkText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            daftarGerakan.forEachIndexed { index, gerakan ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F0FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = gerakan.nama, color = DarkText, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = targetGerakanLabel(gerakan),
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                }
                if (index != daftarGerakan.lastIndex) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = BorderColor)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun TipsSebelumLatihanCard(tips: KontenEntity?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Tips Sebelum Latihan",
                color = DarkText,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (tips == null) {
                Text(
                    text = "Belum ada tips aktif",
                    color = MutedText,
                    fontSize = 13.sp
                )
            } else {
                Text(
                    text = tips.judul,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tips.isi,
                    color = MutedText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun CountdownCard(countdown: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bersiap mulai",
                color = DarkText,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F0FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$countdown",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 42.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { (10 - countdown).coerceAtLeast(0) / 10f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = PrimaryBlue,
                trackColor = BorderColor
            )
        }
    }
}

@Composable
private fun GerakanSetInputCard(
    gerakan: GerakanWorkout,
    nilaiSet: List<String>,
    totalAktual: Int,
    enabled: Boolean,
    onPlayTutorial: (String) -> Unit,
    onSetValueChange: (Int, String) -> Unit
) {
    val progress = if (gerakan.repTarget > 0) {
        (totalAktual.toFloat() / gerakan.repTarget.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val kalori = totalAktual * gerakan.kaloriPerRep

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = gerakan.nama,
                        color = DarkText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = targetGerakanLabel(gerakan),
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "%.2f kcal".format(kalori),
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TutorialVideoCard(
                gerakanNama = gerakan.nama,
                onPlayClick = { onPlayTutorial(gerakan.nama) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (gerakan.tipe == TipeGerakan.DURASI) {
                DurasiTimerSetRows(
                    gerakan = gerakan,
                    nilaiSet = nilaiSet,
                    enabled = enabled,
                    onSetValueChange = onSetValueChange
                )
            } else {
                RepsSetInputRows(
                    gerakan = gerakan,
                    nilaiSet = nilaiSet,
                    enabled = enabled,
                    onSetValueChange = onSetValueChange
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = if (progress >= 1f) Color(0xFF22C55E) else PrimaryBlue,
                trackColor = BorderColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Total selesai: $totalAktual / ${gerakan.repTarget} ${gerakan.unitLabel}",
                color = MutedText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            AnimatedVisibility(visible = gerakan.catatan != null) {
                Text(
                    text = gerakan.catatan.orEmpty(),
                    color = DarkText,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TutorialVideoCard(
    gerakanNama: String,
    onPlayClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F6FF))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Video tutorial",
                color = DarkText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "Lihat contoh gerakan $gerakanNama",
                color = MutedText,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Button(
            onClick = onPlayClick,
            modifier = Modifier.height(38.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text(text = "Play", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun RepsSetInputRows(
    gerakan: GerakanWorkout,
    nilaiSet: List<String>,
    enabled: Boolean,
    onSetValueChange: (Int, String) -> Unit
) {
    nilaiSet.forEachIndexed { index, value ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Set ${index + 1}",
                color = DarkText,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(58.dp)
            )
            OutlinedTextField(
                enabled = enabled,
                value = value,
                onValueChange = { onSetValueChange(index, it) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                ),
                placeholder = {
                    Text(
                        text = "0",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MutedText
                    )
                },
                suffix = {
                    Text(
                        text = gerakan.unitLabel,
                        color = MutedText,
                        fontSize = 12.sp
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderColor
                )
            )
            Text(
                text = "/ ${gerakan.targetPerSet}",
                color = MutedText,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(48.dp)
            )
        }
        if (index != nilaiSet.lastIndex) Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DurasiTimerSetRows(
    gerakan: GerakanWorkout,
    nilaiSet: List<String>,
    enabled: Boolean,
    onSetValueChange: (Int, String) -> Unit
) {
    val jumlahSet = nilaiSet.size
    val targetDetik = gerakan.targetPerSet.coerceAtLeast(1)
    val timerSisa = remember(gerakan.nama, jumlahSet) {
        mutableStateListOf(*Array(jumlahSet) { targetDetik })
    }
    val timerBerjalan = remember(gerakan.nama, jumlahSet) {
        mutableStateListOf(*Array(jumlahSet) { false })
    }

    LaunchedEffect(enabled) {
        if (!enabled) {
            timerBerjalan.indices.forEach { index -> timerBerjalan[index] = false }
        }
    }

    timerBerjalan.indices.forEach { index ->
        LaunchedEffect(enabled, timerBerjalan[index], timerSisa[index]) {
            if (enabled && timerBerjalan[index] && timerSisa[index] > 0) {
                delay(1000)
                val sisaBaru = (timerSisa[index] - 1).coerceAtLeast(0)
                timerSisa[index] = sisaBaru
                onSetValueChange(index, (targetDetik - sisaBaru).toString())
                if (sisaBaru == 0) timerBerjalan[index] = false
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        nilaiSet.indices.forEach { index ->
            DurasiTimerSetRow(
                nomorSet = index + 1,
                targetDetik = targetDetik,
                sisaDetik = timerSisa[index],
                isRunning = timerBerjalan[index],
                enabled = enabled,
                onStartPause = {
                    timerBerjalan[index] = !timerBerjalan[index]
                },
                onReset = {
                    timerBerjalan[index] = false
                    timerSisa[index] = targetDetik
                    onSetValueChange(index, "")
                }
            )
        }
    }
}

@Composable
private fun DurasiTimerSetRow(
    nomorSet: Int,
    targetDetik: Int,
    sisaDetik: Int,
    isRunning: Boolean,
    enabled: Boolean,
    onStartPause: () -> Unit,
    onReset: () -> Unit
) {
    val selesaiDetik = (targetDetik - sisaDetik).coerceIn(0, targetDetik)
    val progressSet = (selesaiDetik.toFloat() / targetDetik.toFloat()).coerceIn(0f, 1f)
    val tombolTimer = when {
        isRunning -> "Pause"
        selesaiDetik > 0 && sisaDetik > 0 -> "Lanjutkan"
        sisaDetik == 0 -> "Selesai"
        else -> "Mulai Timer"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF8FAFC))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Set $nomorSet",
                color = DarkText,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(58.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatCountdownTimer(sisaDetik),
                    color = if (isRunning) PrimaryBlue else DarkText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
            Text(
                text = "/ $targetDetik",
                color = MutedText,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progressSet },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50)),
            color = if (progressSet >= 1f) Color(0xFF22C55E) else PrimaryBlue,
            trackColor = BorderColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tercatat: $selesaiDetik / $targetDetik detik",
            color = MutedText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                enabled = enabled && sisaDetik > 0,
                onClick = onStartPause,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = Color(0xFFE5E7EB),
                    disabledContentColor = MutedText
                )
            ) {
                Text(
                    text = tombolTimer,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            Button(
                enabled = enabled && selesaiDetik > 0,
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE8EDF5),
                    contentColor = DarkText,
                    disabledContainerColor = Color(0xFFE5E7EB),
                    disabledContentColor = MutedText
                )
            ) {
                Text(
                    text = "Reset",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SesiBottomBar(
    statusSesi: StatusSesi,
    onFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Button(
            enabled = statusSesi == StatusSesi.BERJALAN,
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEF4444),
                disabledContainerColor = Color(0xFFE5E7EB),
                disabledContentColor = Color(0xFF6B7280)
            )
        ) {
            Text(
                text = when (statusSesi) {
                    StatusSesi.BELUM_MULAI -> "Mulai latihan dulu"
                    StatusSesi.HITUNG_MUNDUR -> "Countdown berjalan"
                    StatusSesi.BERJALAN -> "Selesai Sesi Ini"
                    StatusSesi.SELESAI -> "Sesi selesai"
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun pilihGerakanSesi(
    fokusLatihan: String,
    arahTargetBerat: String
): List<GerakanWorkout> {
    val fokus = fokusLatihan.lowercase()
    val targetNaik = arahTargetBerat.equals("Menaikkan", ignoreCase = true) ||
        fokus.contains("otot") ||
        fokus.contains("massa")
    val targetTurun = arahTargetBerat.equals("Menurunkan", ignoreCase = true) ||
        fokus.contains("turun berat") ||
        fokus.contains("menurunkan")

    return when {
        targetTurun -> listOf(
            GerakanWorkout(
                nama = "Jumping Jack",
                kaloriPerRep = KaloriPerRep.JUMPING_JACK,
                set = 3,
                repTarget = 75,
                istirahat = "Rest 35 detik",
                catatan = "Jaga ritme napas dan mendarat dengan lutut sedikit menekuk.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "High Knees",
                kaloriPerRep = KaloriPerRep.HIGH_KNEES,
                set = 3,
                repTarget = 90,
                istirahat = "Rest 35 detik",
                catatan = "Angkat lutut setinggi pinggang dan jaga badan tetap tegak.",
                tipe = TipeGerakan.DURASI
            ),
            GerakanWorkout(
                nama = "Mountain Climber",
                kaloriPerRep = KaloriPerRep.MOUNTAIN_CLIMBER,
                set = 3,
                repTarget = 60,
                istirahat = "Rest 40 detik",
                catatan = "Jaga bahu tepat di atas tangan dan core tetap aktif.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Squat",
                kaloriPerRep = KaloriPerRep.SQUAT,
                set = 3,
                repTarget = 45,
                istirahat = "Rest 45 detik",
                catatan = "Dorong pinggul ke belakang, lutut mengikuti arah jari kaki.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Plank",
                kaloriPerRep = KaloriPerRep.PLANK,
                set = 1,
                repTarget = 45,
                catatan = "Tahan posisi core, jangan turunkan pinggul.",
                tipe = TipeGerakan.DURASI
            )
        )

        targetNaik -> listOf(
            GerakanWorkout(
                nama = "Squat",
                kaloriPerRep = KaloriPerRep.SQUAT,
                set = 4,
                repTarget = 40,
                istirahat = "Rest 60 detik",
                catatan = "Turun terkontrol dan dorong kuat dari tumit saat naik.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Lunge",
                kaloriPerRep = KaloriPerRep.LUNGE,
                set = 3,
                repTarget = 30,
                istirahat = "Rest 55 detik",
                catatan = "Jaga torso tegak dan lutut depan tetap stabil.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Push Up",
                kaloriPerRep = KaloriPerRep.PUSH_UP,
                set = 3,
                repTarget = 36,
                istirahat = "Rest 60 detik",
                catatan = "Jaga siku 45 derajat dan tubuh tetap lurus.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Shoulder Tap",
                kaloriPerRep = KaloriPerRep.SHOULDER_TAP,
                set = 3,
                repTarget = 30,
                istirahat = "Rest 45 detik",
                catatan = "Pinggul stabil, bahu tetap sejajar.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Wall Sit",
                kaloriPerRep = KaloriPerRep.WALL_SIT,
                set = 2,
                repTarget = 80,
                istirahat = "Rest 45 detik",
                catatan = "Punggung menempel dinding dan paha sejajar lantai.",
                tipe = TipeGerakan.DURASI
            )
        )

        fokus.contains("stamina") -> listOf(
            GerakanWorkout(
                nama = "High Knees",
                kaloriPerRep = KaloriPerRep.HIGH_KNEES,
                set = 3,
                repTarget = 90,
                istirahat = "Rest 30 detik",
                catatan = "Mulai sedang, naikkan tempo saat tubuh sudah panas.",
                tipe = TipeGerakan.DURASI
            ),
            GerakanWorkout(
                nama = "Burpee",
                kaloriPerRep = KaloriPerRep.BURPEE,
                set = 3,
                repTarget = 24,
                istirahat = "Rest 50 detik",
                catatan = "Jaga pendaratan lembut dan jangan menahan napas.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Mountain Climber",
                kaloriPerRep = KaloriPerRep.MOUNTAIN_CLIMBER,
                set = 3,
                repTarget = 60,
                istirahat = "Rest 40 detik",
                catatan = "Jaga ritme cepat tanpa mengorbankan posisi core.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Jumping Jack",
                kaloriPerRep = KaloriPerRep.JUMPING_JACK,
                set = 3,
                repTarget = 75,
                istirahat = "Rest 35 detik",
                catatan = "Gunakan gerakan penuh dan napas stabil.",
                tipe = TipeGerakan.REPS
            )
        )

        else -> listOf(
            GerakanWorkout(
                nama = "Squat",
                kaloriPerRep = KaloriPerRep.SQUAT,
                set = 3,
                repTarget = 36,
                istirahat = "Rest 45 detik",
                catatan = "Gerakan lower body utama untuk kaki dan pinggul.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Push Up",
                kaloriPerRep = KaloriPerRep.PUSH_UP,
                set = 3,
                repTarget = 30,
                istirahat = "Rest 45 detik",
                catatan = "Turunkan dada terkontrol dan dorong sampai siku lurus.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Lunge",
                kaloriPerRep = KaloriPerRep.LUNGE,
                set = 3,
                repTarget = 24,
                istirahat = "Rest 45 detik",
                catatan = "Langkah cukup jauh agar lutut depan stabil.",
                tipe = TipeGerakan.REPS
            ),
            GerakanWorkout(
                nama = "Plank",
                kaloriPerRep = KaloriPerRep.PLANK,
                set = 1,
                repTarget = 45,
                catatan = "Tahan posisi core, jangan turunkan pinggul.",
                tipe = TipeGerakan.DURASI
            )
        )
    }
}

private fun targetGerakanLabel(gerakan: GerakanWorkout): String {
    return "${gerakan.set} set x ${gerakan.targetPerSet} ${gerakan.unitLabel}"
}

private fun formatCountdownTimer(totalDetik: Int): String {
    val aman = totalDetik.coerceAtLeast(0)
    val menit = aman / 60
    val detik = aman % 60
    return if (menit > 0) "%d:%02d".format(menit, detik) else "$detik detik"
}

private fun formatDurasiDetik(totalDetik: Int): String {
    return when {
        totalDetik <= 0 -> "0d"
        totalDetik < 60 -> "${totalDetik}d"
        totalDetik % 60 == 0 -> "${totalDetik / 60}m"
        else -> "${totalDetik / 60}m ${totalDetik % 60}d"
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFD)
@Composable
fun DetailProgramScreenPreview() {
    FitrackTheme {
        DetailProgramScreen()
    }
}
