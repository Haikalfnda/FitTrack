package com.aplikasis.fittrack.navigation

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

    var loggedInUser by remember { mutableStateOf<UserEntity?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // FITUR 5: State untuk meneruskan hasil sesi ke RingkasanSesiScreen
    var hasilSesiTerakhir by remember { mutableStateOf<HasilSesi?>(null) }

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
                    loggedInUser = user
                    when {
                        user.role == "admin" -> navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        user.isPersonalized -> navController.navigate(Screen.Beranda.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        else -> navController.navigate(Screen.Personalization.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
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
                    streakHari = 12,
                    onLanjutLatihan = { navController.navigate(Screen.ProgramLatihan.route) },
                    onLogoutClick = {
                        loggedInUser = null
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = false }
                        }
                    }
                )
            }
        }

        composable(Screen.ProgramLatihan.route) {
            ProgramLatihanScreen(
                onBackClick = { navController.popBackStack() },
                onMulaiSesi = { navController.navigate(Screen.DetailProgram.route) }
            )
        }

        // ── Detail Program ────────────────────────────────────────────────────
        composable(Screen.DetailProgram.route) {
            val context = LocalContext.current
            DetailProgramScreen(
                onBackClick = { navController.popBackStack() },
                onSelesaiSesi = { hasil ->
                    hasilSesiTerakhir = hasil
                    coroutineScope.launch {
                        val dao = FitTrackDatabase.getDatabase(context).fitTrackDao()
                        val tanggal = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
                        dao.insertRiwayat(
                            RiwayatLatihanEntity(
                                idUser = loggedInUser?.idUser ?: 0L,
                                namaProgram = "Full Body Beginner",
                                tanggal = tanggal,
                                durasi = "25 menit",
                                reps = "${hasil.totalRep} reps",
                                kalori = "%.1f kcal".format(hasil.totalKalori),
                                detail = hasil.detailGerakan.joinToString(", ") {
                                    "${it.nama}: ${it.repSelesai}/${it.repTarget} rep"
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
            ProgressTrackingScreen(
                onBerandaClick = {
                    navController.navigate(Screen.Beranda.route) {
                        popUpTo(Screen.Beranda.route) { inclusive = false }
                    }
                },
                onRiwayatClick = { navController.navigate(Screen.RiwayatLatihan.route) },
                onVideoClick = { navController.navigate(Screen.VideoTutorial.route) }
            )
        }

        composable(Screen.RiwayatLatihan.route) {
            val context = LocalContext.current
            val dao = FitTrackDatabase.getDatabase(context).fitTrackDao()
            val riwayatViewModel: RiwayatLatihanViewModel = viewModel(factory = ViewModelFactory(dao))
            RiwayatLatihanScreen(navController = navController, viewModel = riwayatViewModel)
        }

        composable(Screen.VideoTutorial.route) {
            val context = LocalContext.current
            val dao = remember { FitTrackDatabase.getDatabase(context).fitTrackDao() }
            val videoViewModel: VideoTutorialViewModel = viewModel(factory = ViewModelFactory(dao))
            VideoTutorialScreen(
                viewModel = videoViewModel,
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
            FormKontenScreen(idKonten = idKonten, onBackClick = { navController.popBackStack() })
        }

        composable(Screen.KelolaVideo.route) {
            KelolaVideoScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.DataPengguna.route) {
            DataPenggunaScreen(onBackClick = { navController.popBackStack() })
        }
    }
}