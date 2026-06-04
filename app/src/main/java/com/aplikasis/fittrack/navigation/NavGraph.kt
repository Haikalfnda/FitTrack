package com.aplikasis.fittrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aplikasis.fittrack.data.database.FitTrackDatabase
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity
import com.aplikasis.fittrack.ui.screen.BerandaScreen
import com.aplikasis.fittrack.ui.screen.DashboardAdminScreen
import com.aplikasis.fittrack.ui.screen.DataPenggunaScreen
import com.aplikasis.fittrack.ui.screen.DetailProgramScreen
import com.aplikasis.fittrack.ui.screen.FormKontenScreen
import com.aplikasis.fittrack.ui.screen.KelolaKontenScreen
import com.aplikasis.fittrack.ui.screen.KelolaVideoScreen
import com.aplikasis.fittrack.ui.screen.LoginScreen
import com.aplikasis.fittrack.ui.screen.PersonalizationScreen
import com.aplikasis.fittrack.ui.screen.ProgramLatihanScreen
import com.aplikasis.fittrack.ui.screen.ProgressTrackingScreen
import com.aplikasis.fittrack.ui.screen.RegisterScreen
import com.aplikasis.fittrack.ui.screen.RingkasanSesiScreen
import com.aplikasis.fittrack.ui.screen.RiwayatLatihanScreen
import com.aplikasis.fittrack.ui.screen.RiwayatLatihanViewModel
import com.aplikasis.fittrack.ui.screen.VideoDetailScreen
import com.aplikasis.fittrack.ui.screen.VideoTutorialScreen
import com.aplikasis.fittrack.ui.screen.VideoTutorialViewModel
import com.aplikasis.fittrack.ui.screen.ViewModelFactory
import com.aplikasis.fittrack.ui.screen.WelcomeScreen

@Composable
fun NavGraph(navController: NavHostController) {

    // Simpan user yang sedang login di sini
    var loggedInUser by remember { mutableStateOf<UserEntity?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { user ->
                    loggedInUser = user  // simpan user asli di sini
                    if (user.role == "admin") {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Personalization.route) {
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
            PersonalizationScreen(
                onBackClick = { navController.popBackStack() },
                onCreateProgramClick = {
                    navController.navigate(Screen.Beranda.route) {
                        popUpTo(Screen.Personalization.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Beranda User ──────────────────────────────────────────────────────
        composable(Screen.Beranda.route) {
            val user = loggedInUser
            if (user != null) {
                BerandaScreen(
                    user = user,
                    navController = navController,
                    streakHari = 12,
                    onLanjutLatihan = {
                        navController.navigate(Screen.ProgramLatihan.route)
                    }
                )
            }
        }

        // ── Program Latihan ───────────────────────────────────────────────────
        composable(Screen.ProgramLatihan.route) {
            ProgramLatihanScreen(
                onBackClick = { navController.popBackStack() },
                onMulaiSesi = {
                    navController.navigate(Screen.DetailProgram.route)
                }
            )
        }

        // ── Detail Program ────────────────────────────────────────────────────
        composable(Screen.DetailProgram.route) {
            DetailProgramScreen(
                onBackClick = { navController.popBackStack() },
                onSelesaiSesi = {
                    navController.navigate(Screen.RingkasanSesi.route)
                }
            )
        }

        // ── Ringkasan Sesi ────────────────────────────────────────────────────
        composable(Screen.RingkasanSesi.route) {
            RingkasanSesiScreen(
                onLihatRiwayat = {
                    navController.navigate(Screen.RiwayatLatihan.route)
                },
                onBukaProgressTracking = {
                    navController.navigate(Screen.ProgressTracking.route)
                }
            )
        }

        // ── Progress Tracking ─────────────────────────────────────────────────
        composable(Screen.ProgressTracking.route) {
            ProgressTrackingScreen(
                onBerandaClick = {
                    navController.navigate(Screen.Beranda.route) {
                        popUpTo(Screen.Beranda.route) { inclusive = false }
                    }
                },
                onRiwayatClick = {
                    navController.navigate(Screen.RiwayatLatihan.route)
                },
                onVideoClick = {
                    navController.navigate(Screen.VideoTutorial.route)
                }
            )
        }
        
        // ── Riwayat Latihan ───────────────────────────────────────────────────
        composable("riwayat_latihan") {
            val context = LocalContext.current

            // 1. Ambil instance database & DAO Anda
            val database = FitTrackDatabase.getDatabase(context) // Menggunakan fungsi singleton DB Anda
            val dao = database.fitTrackDao() // Sesuaikan dengan nama fungsi return DAO di DB Anda

            // 2. Inisialisasi ViewModel menggunakan Factory kustom
            val riwayatViewModel: RiwayatLatihanViewModel = viewModel(
                factory = ViewModelFactory(dao)
            )

            // 3. Teruskan ke Screen Anda
            RiwayatLatihanScreen(
                navController = navController,
                viewModel = riwayatViewModel
            )
        }

        // ── Video Tutorial ────────────────────────────────────────────────────
        composable(Screen.VideoTutorial.route) {

            val context = LocalContext.current

            val dao = remember {
                FitTrackDatabase
                    .getDatabase(context)
                    .fitTrackDao()
            }

            val videoViewModel: VideoTutorialViewModel = viewModel(
                factory = ViewModelFactory(dao)
            )
            val video =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<VideoTutorialEntity>("video")

            if (video != null) {
                VideoDetailScreen(
                    videoId = videoId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            VideoTutorialScreen(
                viewModel = videoViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onVideoClick = { video ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("video", video)

                    navController.navigate(Screen.DetailVideo.route)
                }
            )
        }

        composable(
            route = "detail_video/{videoId}",
            arguments = listOf(
                navArgument("videoId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val context = LocalContext.current

            val dao = remember {
                FitTrackDatabase
                    .getDatabase(context)
                    .fitTrackDao()
            }

            val videoViewModel: VideoTutorialViewModel = viewModel(
                factory = ViewModelFactory(dao)
            )

            val videoId =
                backStackEntry.arguments?.getLong("videoId") ?: 0L

            VideoDetailScreen(
                viewModel = videoViewModel,
                videoId = videoId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // ── Admin ─────────────────────────────────────────────────────────────
        composable(Screen.AdminDashboard.route) {
            DashboardAdminScreen(
                onKelolaKontenClick = { navController.navigate(Screen.KelolaKonten.route) },
                onKelolaVideoClick = { navController.navigate(Screen.KelolaVideo.route) },
                onDataPenggunaClick = { navController.navigate(Screen.DataPengguna.route) },
                onLogoutClick = {
                    loggedInUser = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminDashboard.route) { inclusive = true }
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
            arguments = listOf(
                navArgument("idKonten") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val idKonten = backStackEntry.arguments?.getLong("idKonten") ?: 0L
            FormKontenScreen(
                idKonten = idKonten,
                onBackClick = { navController.popBackStack() }
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
