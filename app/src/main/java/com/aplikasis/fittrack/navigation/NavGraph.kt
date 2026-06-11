package com.aplikasis.fittrack.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aplikasis.fittrack.data.database.FitTrackDatabase
import com.aplikasis.fittrack.data.entity.RiwayatLatihanEntity
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.model.HasilSesi
import com.aplikasis.fittrack.ui.screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NavGraph(navController: NavHostController) {

    val appContext = LocalContext.current.applicationContext
    var loggedInUser by remember { mutableStateOf<UserEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // FITUR 5: State untuk meneruskan hasil sesi ke RingkasanSesiScreen
    var hasilSesiTerakhir by remember { mutableStateOf<HasilSesi?>(null) }

    LaunchedEffect(Unit) {
        val savedUser = withContext(Dispatchers.IO) {
            restoreSavedSession(appContext)
        }
        if (savedUser != null && loggedInUser == null) {
            loggedInUser = savedUser
            navController.navigate(destinationForUser(savedUser)) {
                popUpTo(Screen.Welcome.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {

        composable(Screen.Welcome.route) {
            WelcomeScreen(onStartClick = { navController.navigate(Screen.Login.route) })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { user ->
                    saveSession(appContext, user)
                    loggedInUser = user
                    navController.navigate(destinationForUser(user)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Personalization.route) {
            val context = LocalContext.current
            val dao = FitTrackDatabase.getDatabase(context).fitTrackDao()
            val idUserAktif = loggedInUser?.idUser ?: 0L
            PersonalizationScreen(
                fitTrackDao = dao,
                idUserAktif = idUserAktif,
                onBackClick = { navController.popBackStack() },
                onCreateProgramClick = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) { dao.setPersonalized(idUserAktif) }
                        loggedInUser = loggedInUser?.copy(isPersonalized = true)
                        loggedInUser?.let { saveSession(appContext, it) }
                        navController.navigate(Screen.Beranda.route) {
                            popUpTo(Screen.Personalization.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Beranda.route) {
            val user = loggedInUser
            if (user != null) {
                val context = LocalContext.current
                val dao = FitTrackDatabase.getDatabase(context).fitTrackDao()
                BerandaScreen(
                    fitTrackDao = dao,
                    idUserAktif = user.idUser,
                    user = user,
                    navController = navController,
                    onLanjutLatihan = { navController.navigate(Screen.ProgramLatihan.route) },
                    onLogoutClick = {
                        clearSession(appContext)
                        loggedInUser = null
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = false }
                        }
                    }
                )
            }
        }

        composable(
            route = Screen.DetailArtikel.route,
            arguments = listOf(navArgument("idKonten") { type = NavType.LongType })
        ) { backStackEntry ->
            val context = LocalContext.current
            val dao = remember { FitTrackDatabase.getDatabase(context).fitTrackDao() }
            val idKonten = backStackEntry.arguments?.getLong("idKonten") ?: 0L
            DetailArtikelScreen(
                fitTrackDao = dao,
                idKonten = idKonten,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ProgramLatihan.route) {
            val user = loggedInUser
            if (user != null) {
                val context = LocalContext.current
                val dao = FitTrackDatabase.getDatabase(context).fitTrackDao()
                ProgramLatihanScreen(
                    fitTrackDao = dao,
                    idUserAktif = user.idUser,
                    user = user,
                    onBackClick = { navController.popBackStack() },
                    onMulaiSesi = { navController.navigate(Screen.DetailProgram.route) }
                )
            }
        }

        // ── Detail Program ────────────────────────────────────────────────────
        composable(Screen.DetailProgram.route) {
            val context = LocalContext.current
            val dao = remember { FitTrackDatabase.getDatabase(context).fitTrackDao() }
            val tipsAktif by dao.getTipsAktif().collectAsState(initial = emptyList())
            val user = loggedInUser
            val levelLatihan = user?.level?.takeIf { it.isNotBlank() } ?: "Pemula"
            val fokusLatihan = user?.tujuan?.takeIf { it.isNotBlank() } ?: "Full body dan core"
            val arahTargetBerat = user?.arahTargetBerat.orEmpty()
            val namaProgram = when {
                arahTargetBerat.equals("Menurunkan", ignoreCase = true) ||
                    fokusLatihan.contains("turun", ignoreCase = true) -> "Fat Loss $levelLatihan"
                arahTargetBerat.equals("Menaikkan", ignoreCase = true) -> "Strength Builder $levelLatihan"
                fokusLatihan.contains("otot", ignoreCase = true) -> "Strength Builder $levelLatihan"
                fokusLatihan.contains("stamina", ignoreCase = true) -> "Stamina Circuit $levelLatihan"
                fokusLatihan.contains("kebugaran", ignoreCase = true) -> "Fit Maintenance $levelLatihan"
                else -> "Full Body $levelLatihan"
            }
            DetailProgramScreen(
                namaProgram = namaProgram,
                hariLatihan = "Sesi hari ini",
                fokusLatihan = fokusLatihan,
                levelLatihan = levelLatihan,
                arahTargetBerat = arahTargetBerat,
                tipsSebelumLatihan = tipsAktif.firstOrNull(),
                onBackClick = { navController.popBackStack() },
                onPlayTutorial = { gerakan ->
                    navController.navigate(Screen.VideoTutorial.createRoute(gerakan)) {
                        launchSingleTop = true
                    }
                },
                onSelesaiSesi = { hasil ->
                    hasilSesiTerakhir = hasil
                    coroutineScope.launch {
                        val dao = FitTrackDatabase.getDatabase(context).fitTrackDao()
                        val tanggal = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
                        dao.insertRiwayat(
                            RiwayatLatihanEntity(
                                idUser = loggedInUser?.idUser ?: 0L,
                                namaProgram = hasil.namaProgram,
                                tanggal = tanggal,
                                durasi = hasil.durasiLabel,
                                reps = "${hasil.totalRep} reps",
                                kalori = "%.1f kcal".format(hasil.totalKalori),
                                detail = hasil.detailGerakan.joinToString(", ") {
                                    val setDetail = it.setLabel.takeIf { label -> label.isNotBlank() }
                                        ?.let { label -> " ($label)" }
                                        ?: ""
                                    "${it.nama}: ${it.progressLabel}$setDetail"
                                },
                                tipeFilter = "Mingguan"
                            )
                        )
                        navController.navigate(Screen.RingkasanSesi.route)
                    }
                }
            )
        }

        // ── Ringkasan Sesi ────────────────────────────────────────────────────
        composable(Screen.RingkasanSesi.route) {
            RingkasanSesiScreen(
                hasilSesi = hasilSesiTerakhir,
                onLihatRiwayat = { navController.navigate(Screen.RiwayatLatihan.route) },
                onBukaProgressTracking = { navController.navigate(Screen.ProgressTracking.route) }
            )
        }

        composable(Screen.ProgressTracking.route) {
            val context = LocalContext.current
            val dao = FitTrackDatabase.getDatabase(context).fitTrackDao()
            ProgressTrackingScreen(
                fitTrackDao = dao,
                idUserAktif = loggedInUser?.idUser ?: 0L,
                onBerandaClick = {
                    navController.navigate(Screen.Beranda.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onRiwayatClick = {
                    navController.navigate(Screen.RiwayatLatihan.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onVideoClick = {
                    navController.navigate(Screen.VideoTutorial.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.RiwayatLatihan.route) {
            val context = LocalContext.current
            val dao = FitTrackDatabase.getDatabase(context).fitTrackDao()
            val riwayatViewModel: RiwayatLatihanViewModel = viewModel(
                factory = ViewModelFactory(dao, loggedInUser?.idUser ?: 0L)
            )
            RiwayatLatihanScreen(navController = navController, viewModel = riwayatViewModel)
        }

        composable(
            route = Screen.VideoTutorial.routeWithGerakan,
            arguments = listOf(
                navArgument(Screen.VideoTutorial.argGerakan) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val dao = remember { FitTrackDatabase.getDatabase(context).fitTrackDao() }
            val videoViewModel: VideoTutorialViewModel = viewModel(factory = ViewModelFactory(dao))
            val gerakanAwal = backStackEntry.arguments?.getString(Screen.VideoTutorial.argGerakan)
            VideoTutorialScreen(
                viewModel = videoViewModel,
                initialGerakan = gerakanAwal,
                onBackClick = { navController.popBackStack() },
                onVideoClick = { video -> navController.navigate(Screen.DetailVideo.createRoute(video.idVideo)) }
            )
        }

        composable(
            route = Screen.DetailVideo.route,
            arguments = listOf(navArgument("videoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val context = LocalContext.current
            val dao = remember { FitTrackDatabase.getDatabase(context).fitTrackDao() }
            val detailViewModel: VideoDetailViewModel = viewModel(factory = ViewModelFactory(dao))
            val videoId = backStackEntry.arguments?.getLong("videoId") ?: 0L
            VideoDetailScreen(viewModel = detailViewModel, videoId = videoId, onBackClick = { navController.popBackStack() })
        }

        composable(Screen.AdminDashboard.route) {
            DashboardAdminScreen(
                onKelolaKontenClick = { navController.navigate(Screen.KelolaKonten.route) },
                onKelolaVideoClick = { navController.navigate(Screen.KelolaVideo.route) },
                onDataPenggunaClick = { navController.navigate(Screen.DataPengguna.route) },
                onLogoutClick = {
                    clearSession(appContext)
                    loggedInUser = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.KelolaKonten.route) {
            KelolaKontenScreen(
                onBackClick = { navController.popBackStack() },
                onTambahClick = { navController.navigate(Screen.FormKonten.createRoute(0L)) },
                onEditClick = { idKonten -> navController.navigate(Screen.FormKonten.createRoute(idKonten)) }
            )
        }

        composable(
            route = Screen.FormKonten.route,
            arguments = listOf(navArgument("idKonten") { type = NavType.LongType; defaultValue = 0L })
        ) { backStackEntry ->
            val idKonten = backStackEntry.arguments?.getLong("idKonten") ?: 0L
            FormKontenScreen(
                idKonten = idKonten,
                onBackClick = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Screen.KelolaVideo.route) {
            KelolaVideoScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.DataPengguna.route) {
            DataPenggunaScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

private const val SESSION_PREF = "fittrack_session"
private const val KEY_USER_ID = "user_id"

private suspend fun restoreSavedSession(context: Context): UserEntity? {
    val savedId = context
        .getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE)
        .getLong(KEY_USER_ID, 0L)

    if (savedId <= 0L) return null

    val dao = FitTrackDatabase.getDatabase(context).fitTrackDao()
    val user = dao.getUserByIdOnce(savedId)

    return if (user != null && user.status.equals("aktif", ignoreCase = true)) {
        user
    } else {
        clearSession(context)
        null
    }
}

private fun saveSession(context: Context, user: UserEntity) {
    if (!user.status.equals("aktif", ignoreCase = true)) return

    context
        .getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE)
        .edit()
        .putLong(KEY_USER_ID, user.idUser)
        .apply()
}

private fun clearSession(context: Context) {
    context
        .getSharedPreferences(SESSION_PREF, Context.MODE_PRIVATE)
        .edit()
        .clear()
        .apply()
}

private fun destinationForUser(user: UserEntity): String {
    return when {
        user.role == "admin" -> Screen.AdminDashboard.route
        user.isPersonalized -> Screen.Beranda.route
        else -> Screen.Personalization.route
    }
}
