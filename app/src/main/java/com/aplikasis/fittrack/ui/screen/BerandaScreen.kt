package com.aplikasis.fittrack.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.outlined.History
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aplikasis.fittrack.data.dao.FitTrackDao
import com.aplikasis.fittrack.data.entity.KontenEntity
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.model.LatihanStats
import com.aplikasis.fittrack.model.RingkasanLatihan
import com.aplikasis.fittrack.navigation.Screen
import com.aplikasis.fittrack.ui.theme.*

// ─── Data Model Ringkasan Cepat ───────────────────────────────────────────────
// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun BerandaScreen(
    fitTrackDao: FitTrackDao,      // Parameter Baru untuk akses DB
    idUserAktif: Long,             // Parameter Baru untuk identifikasi User
    user: UserEntity,
    navController: NavController,
    onLanjutLatihan: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // 1. Mengamati data user & jumlah latihan riwayat mingguan secara real-time dari Room
    val userLiveState by fitTrackDao.getUserById(idUserAktif).collectAsState(initial = user)
    val riwayatUser by fitTrackDao.getRiwayatByUser(idUserAktif).collectAsState(initial = emptyList())
    val bannerAktif by fitTrackDao.getBannerAktif().collectAsState(initial = emptyList())
    val tipsAktif by fitTrackDao.getTipsAktif().collectAsState(initial = emptyList())
    val artikelAktif by fitTrackDao.getArtikelAktif().collectAsState(initial = emptyList())

    // 2. Gunakan data objek live state dari database
    val currentUser = userLiveState ?: user
    val hariTargetDb = currentUser.targetHariPerMinggu.coerceAtLeast(0)
    val ringkasan = remember(riwayatUser) { LatihanStats.hitungRingkasan(riwayatUser) }
    val hariSelesaiDb = remember(riwayatUser) { LatihanStats.hitungHariLatihanMingguIni(riwayatUser) }
    val streakHariDb = remember(riwayatUser) { LatihanStats.hitungStreakSaatIni(riwayatUser) }

    // 3. Hitung persentase progres mingguan secara dinamis & kunci maksimal di angka 100
    val progressMingguDb = if (hariTargetDb > 0) {
        val hitungPersen = (hariSelesaiDb.toFloat() / hariTargetDb.toFloat() * 100).toInt()
        hitungPersen.coerceAtMost(100) // Memastikan nilai teks tidak melampaui 100%
    } else {
        0
    }
    val statusMinggu = when {
        hariTargetDb <= 0 -> "Lengkapi personalisasi untuk target mingguan."
        hariSelesaiDb >= hariTargetDb -> "Target minggu ini tercapai."
        else -> "${hariTargetDb - hariSelesaiDb} hari latihan lagi untuk target minggu ini."
    }

    Scaffold(
        containerColor = ScreenBg,
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // ── Header (Menggunakan Data Ril DB) ──────────────────────────────────
            HeaderCard(
                namaUser = currentUser.nama,
                streakHari = streakHariDb,
                hariSelesai = hariSelesaiDb, // Tetap biarkan menampilkan angka asli (contoh: Target 4/3)
                hariTarget = hariTargetDb,
                progressMinggu = progressMingguDb,
                statusMinggu = statusMinggu,
                onLogoutClick = onLogoutClick
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

            // Mengoper variabel real database ke dalam card program aktif
            ProgramAktifCard(
                namaProgram = namaProgramBeranda(currentUser),
                frekuensi = "${targetHariLabel(hariTargetDb)} - ${currentUser.durasiLatihan.ifBlank { "Durasi belum diatur" }} - Personal",
                tipeLatihan = rekomendasiLatihanHariIni(currentUser.tujuan, currentUser.arahTargetBerat),
                progressMinggu = progressMingguDb,
                levelLabel = currentUser.level.ifBlank { "Level belum diisi" },
                tujuanLabel = currentUser.tujuan.takeIf { it.isNotBlank() }?.let { "Goal: $it" } ?: "Goal belum diisi",
                targetBeratLabel = targetBeratLabel(currentUser),
                nextProgression = if (progressMingguDb >= 100) "atur target baru" else "+2 reps",
                onLanjutLatihan = onLanjutLatihan,
                navController = navController
            )

            Spacer(modifier = Modifier.height(20.dp))

            BannerKontenSection(banner = bannerAktif.firstOrNull())

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

            RingkasanCepatRow(ringkasan = ringkasan)

            Spacer(modifier = Modifier.height(20.dp))

            TipsHariIniSection(tips = tipsAktif.firstOrNull())

            Spacer(modifier = Modifier.height(20.dp))

            ArtikelFitnessSection(
                artikelList = artikelAktif,
                onArtikelClick = { artikel ->
                    navController.navigate(Screen.DetailArtikel.createRoute(artikel.idKonten))
                }
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("Beranda", Screen.Beranda.route, Icons.Default.Home),
            Triple("Riwayat", Screen.RiwayatLatihan.route, Icons.Outlined.History),
            Triple("Progres", Screen.ProgressTracking.route, Icons.Default.ShowChart),
            Triple("Video", Screen.VideoTutorial.route, Icons.Default.PlayArrow)
        )
        items.forEach { (label, route, icon) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Screen.Beranda.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = Color(0xFFE8F0FD),
                    unselectedIconColor = MutedText,
                    unselectedTextColor = MutedText
                )
            )
        }
    }
}

// ─── Header Card ─────────────────────────────────────────────────────────────

@Composable
private fun HeaderCard(
    namaUser: String,
    streakHari: Int,
    hariSelesai: Int,
    hariTarget: Int,
    progressMinggu: Int,
    statusMinggu: String,
    onLogoutClick: () -> Unit
) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    // Trigger animasi bar setiap ada perubahan data persen progres
    LaunchedEffect(progressMinggu) {
        animatedProgress = if (progressMinggu > 0) (progressMinggu / 100f).coerceAtMost(1f) else 0f
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Halo, ${namaUser.split(" ").firstOrNull() ?: "User"} 👋",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )

                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text(text = "🚪", fontSize = 16.sp)
                        }
                    }
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (hariTarget > 0) "Target $hariSelesai/$hariTarget" else "Target belum diatur",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "$progressMinggu%",
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
                text = statusMinggu,
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
    namaProgram: String,
    frekuensi: String,
    tipeLatihan: String,
    progressMinggu: Int,
    levelLabel: String,
    tujuanLabel: String,
    targetBeratLabel: String,
    nextProgression: String,
    onLanjutLatihan: () -> Unit,
    navController: NavController // Perbaikan 1: Tipe data ditulis secara eksplisit
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
            Text(
                text = namaProgram,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            )
            Text(
                text = frekuensi,
                style = MaterialTheme.typography.bodySmall.copy(color = MutedText),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                    text = "$progressMinggu%",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (progressMinggu >= 100) Color(0xFF27A844) else PrimaryBlue
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { if (progressMinggu > 0) (progressMinggu / 100f).coerceAtMost(1f) else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progressMinggu >= 100) Color(0xFF27A844) else PrimaryBlue,
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
                    text = tipeLatihan,
                    style = MaterialTheme.typography.bodySmall.copy(color = DarkText),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipLabel(text = levelLabel, color = Color(0xFF6D79FF))
                    ChipLabel(text = tujuanLabel, color = Color(0xFF24C6C2))
                }
                if (targetBeratLabel.isNotBlank()) {
                    Row {
                        ChipLabel(text = targetBeratLabel, color = Color(0xFFE05A24))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next progression: $nextProgression",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MutedText,
                        fontSize = 11.sp
                    )
                )

                // Mengatur sifat klik tombol secara dinamis berdasarkan nilai progress
                Button(
                    onClick = {
                        if (progressMinggu >= 100) {
                            navController.navigate(Screen.Personalization.route) {
                                popUpTo(Screen.Beranda.route) { inclusive = false }
                            }
                        } else {
                            onLanjutLatihan()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (progressMinggu >= 100) Color(0xFF27A844) else PrimaryBlue
                    ),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (progressMinggu >= 100) "Latihan Baru" else "Lanjut latihan",
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
private fun RingkasanCepatRow(
    ringkasan: RingkasanLatihan
) {
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
            value = "%,d".format(ringkasan.totalKalori),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.Timer,
            iconColor = Color(0xFF24C6C2),
            label = "Durasi",
            value = ringkasan.durasiLabel,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BannerKontenSection(banner: KontenEntity?) {
    SectionTitle(text = "Banner Latihan")
    Spacer(modifier = Modifier.height(10.dp))

    if (banner == null) {
        EmptyKontenCard(message = "Belum ada banner aktif")
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = banner.judul,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = banner.isi,
                    color = Color.White.copy(alpha = 0.86f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun TipsHariIniSection(tips: KontenEntity?) {
    SectionTitle(text = "Tips Hari Ini")
    Spacer(modifier = Modifier.height(10.dp))

    if (tips == null) {
        EmptyKontenCard(message = "Belum ada tips aktif")
    } else {
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
                    text = tips.judul,
                    color = DarkText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tips.isi,
                    color = MutedText,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun ArtikelFitnessSection(
    artikelList: List<KontenEntity>,
    onArtikelClick: (KontenEntity) -> Unit
) {
    SectionTitle(text = "Artikel Fitness")
    Spacer(modifier = Modifier.height(10.dp))

    if (artikelList.isEmpty()) {
        EmptyKontenCard(message = "Belum ada artikel fitness")
    } else {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            artikelList.forEach { artikel ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onArtikelClick(artikel) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = artikel.judul,
                            color = DarkText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = potonganKonten(artikel.isi),
                            color = MutedText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Baca Selengkapnya",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontSize = 17.sp
        ),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun EmptyKontenCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = message,
            color = MutedText,
            fontSize = 13.sp,
            modifier = Modifier.padding(16.dp)
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

private fun targetHariLabel(targetHari: Int): String {
    return if (targetHari > 0) {
        "${targetHari}x per minggu"
    } else {
        "Target belum diatur"
    }
}

private fun potonganKonten(isi: String, batas: Int = 120): String {
    val rapi = isi.trim().replace(Regex("\\s+"), " ")
    return if (rapi.length <= batas) rapi else rapi.take(batas).trimEnd() + "..."
}

private fun targetBeratLabel(user: UserEntity): String {
    if (user.beratBadanSaatIni <= 0.0 || user.targetBeratBadan <= 0.0 || user.arahTargetBerat.isBlank()) {
        return ""
    }

    val selisih = kotlin.math.abs(user.targetBeratBadan - user.beratBadanSaatIni)
    val arah = if (user.arahTargetBerat.equals("Menaikkan", ignoreCase = true)) "Naik" else "Turun"
    return "Target: $arah ${formatBeratKgBeranda(selisih)} kg ke ${formatBeratKgBeranda(user.targetBeratBadan)} kg"
}

private fun formatBeratKgBeranda(value: Double): String {
    val formatted = "%.1f".format(java.util.Locale.US, value)
    return formatted.removeSuffix(".0")
}

private fun namaProgramBeranda(user: UserEntity): String {
    val level = user.level.ifBlank { "Personal" }
    return when {
        user.arahTargetBerat.equals("Menurunkan", ignoreCase = true) ||
            user.tujuan.contains("turun", ignoreCase = true) -> "Fat Loss $level"
        user.arahTargetBerat.equals("Menaikkan", ignoreCase = true) ||
            user.tujuan.contains("otot", ignoreCase = true) -> "Strength Builder $level"
        user.tujuan.contains("stamina", ignoreCase = true) -> "Stamina Circuit $level"
        user.tujuan.contains("kebugaran", ignoreCase = true) -> "Fit Maintenance $level"
        else -> "Full Body $level"
    }
}

private fun rekomendasiLatihanHariIni(tujuan: String, arahTargetBerat: String): String {
    return when {
        arahTargetBerat.equals("Menurunkan", ignoreCase = true) ||
            tujuan.contains("turun", ignoreCase = true) -> "Hari ini: Jumping jack + High knees + Squat"
        arahTargetBerat.equals("Menaikkan", ignoreCase = true) -> "Hari ini: Squat + Lunge + Push up"
        tujuan.contains("otot", ignoreCase = true) -> "Hari ini: Push up + Squat + Core"
        tujuan.contains("stamina", ignoreCase = true) -> "Hari ini: Burpee + Mountain climber"
        tujuan.contains("kebugaran", ignoreCase = true) -> "Hari ini: Full body ringan"
        else -> "Hari ini: Push up + Squat + Plank"
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8FAFD)
@Composable
fun BerandaScreenPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val previewDb = androidx.room.Room.inMemoryDatabaseBuilder(
        context,
        com.aplikasis.fittrack.data.database.FitTrackDatabase::class.java
    ).build()

    FitrackTheme {
        BerandaScreen(
            fitTrackDao = previewDb.fitTrackDao(),
            idUserAktif = 1L,
            user = UserEntity(
                idUser = 1,
                nama = "Fabio Santoso",
                email = "fabio@email.com",
                password = "",
                level = "Pemula",
                tujuan = "Turun berat badan",
                targetHariPerMinggu = 3
            ),
            navController = rememberNavController()
        )
    }
}
