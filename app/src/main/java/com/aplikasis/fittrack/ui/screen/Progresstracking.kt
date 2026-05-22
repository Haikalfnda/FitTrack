package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.ui.theme.*

// ─── Data dummy ───────────────────────────────────────────────────────────────

private data class ProgressData(
    val trenRep: List<Int>,          // 6 minggu
    val persenNaik: Int,
    val rataRataReps: Int,
    val diffVsMingguLalu: Int,
    val kaloriTerbakar: Int,
    val statusKalori: String,
    val frekuensiPerMinggu: Int,
    val statusFrekuensi: String
)

private val dummyProgress = ProgressData(
    trenRep = listOf(60, 72, 80, 88, 95, 110),
    persenNaik = 34,
    rataRataReps = 118,
    diffVsMingguLalu = 12,
    kaloriTerbakar = 2840,
    statusKalori = "Stabil",
    frekuensiPerMinggu = 4,
    statusFrekuensi = "Sangat konsisten"
)

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun ProgressTrackingScreen(
    onBerandaClick: () -> Unit = {},
    onRiwayatClick: () -> Unit = {},
    onVideoClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val data = dummyProgress

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selected = "progres",
                onBerandaClick = onBerandaClick,
                onRiwayatClick = onRiwayatClick,
                onVideoClick = onVideoClick
            )
        },
        containerColor = ScreenBg
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Judul ────────────────────────────────────────────────────────
            Text(
                text = "Progress tracking",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Grafik mengambil data otomatis dari riwayat supaya user bisa melihat tren peningkatan secara visual.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MutedText,
                    lineHeight = 18.sp
                ),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Grafik Tren ──────────────────────────────────────────────────
            TrenRepCard(
                data = data.trenRep,
                persenNaik = data.persenNaik
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Stat Cards ───────────────────────────────────────────────────
            StatInfoCard(
                judul = "Rata-rata reps",
                nilai = "${data.rataRataReps} reps minggu ini",
                badge = "+${data.diffVsMingguLalu} vs minggu lalu",
                badgeColor = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(10.dp))

            StatInfoCard(
                judul = "Kalori terbakar",
                nilai = "${"%,d".format(data.kaloriTerbakar)} estimasi otomatis",
                badge = data.statusKalori,
                badgeColor = Color(0xFFF59E0B)
            )

            Spacer(modifier = Modifier.height(10.dp))

            StatInfoCard(
                judul = "Frekuensi",
                nilai = "${data.frekuensiPerMinggu}x / minggu",
                badge = data.statusFrekuensi,
                badgeColor = Color(0xFF22C55E)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Tombol Halaman Utama ──────────────────────────────────────────
            Button(
                onClick = onBerandaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    text = "Halaman Utama",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─── Grafik Tren Rep ──────────────────────────────────────────────────────────

@Composable
private fun TrenRepCard(data: List<Int>, persenNaik: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tren rep 6 minggu",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+$persenNaik%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas grafik garis
            LineChart(
                data = data,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Label W1-W6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("W1", "W2", "W3", "W4", "W5", "W6").forEach { label ->
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
    }
}

@Composable
private fun LineChart(data: List<Int>, modifier: Modifier = Modifier) {
    val lineColor = PrimaryBlue
    val dotColor = PrimaryBlue

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val minVal = data.min().toFloat()
        val maxVal = data.max().toFloat()
        val range = (maxVal - minVal).coerceAtLeast(1f)
        val padding = 12.dp.toPx()

        val points = data.mapIndexed { i, v ->
            val x = if (data.size > 1) i * (w - padding * 2) / (data.size - 1) + padding else w / 2
            val y = h - padding - ((v - minVal) / range) * (h - padding * 2)
            Offset(x, y)
        }

        // Area gradient bawah garis
        val fillPath = Path().apply {
            moveTo(points.first().x, h)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, h)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.15f), Color.Transparent)
            )
        )

        // Garis
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Titik
        points.forEach { point ->
            drawCircle(color = Color.White, radius = 6.dp.toPx(), center = point)
            drawCircle(color = dotColor, radius = 4.dp.toPx(), center = point)
        }
    }
}

// ─── Stat Info Card ───────────────────────────────────────────────────────────

@Composable
private fun StatInfoCard(
    judul: String,
    nilai: String,
    badge: String,
    badgeColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = judul,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = nilai,
                    style = MaterialTheme.typography.bodySmall.copy(color = MutedText)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

// ─── Bottom Nav Bar ───────────────────────────────────────────────────────────

@Composable
fun BottomNavBar(
    selected: String,
    onBerandaClick: () -> Unit,
    onRiwayatClick: () -> Unit,
    onVideoClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            selected = selected == "beranda",
            onClick = onBerandaClick,
            icon = {
                Icon(Icons.Default.Home, contentDescription = "Beranda")
            },
            label = { Text("Beranda", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = Color(0xFFE8F0FD)
            )
        )
        NavigationBarItem(
            selected = selected == "riwayat",
            onClick = onRiwayatClick,
            icon = {
                Icon(Icons.Outlined.History, contentDescription = "Riwayat")
            },
            label = { Text("Riwayat", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = Color(0xFFE8F0FD)
            )
        )
        NavigationBarItem(
            selected = selected == "progres",
            onClick = { /* sudah di sini */ },
            icon = {
                Icon(Icons.Default.ShowChart, contentDescription = "Progres")
            },
            label = { Text("Progres", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = Color(0xFFE8F0FD)
            )
        )
        NavigationBarItem(
            selected = selected == "video",
            onClick = onVideoClick,
            icon = {
                Icon(Icons.Default.PlayArrow, contentDescription = "Video")
            },
            label = { Text("Video", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = Color(0xFFE8F0FD)
            )
        )
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFD)
@Composable
fun ProgressTrackingScreenPreview() {
    FitrackTheme {
        ProgressTrackingScreen()
    }
}