package com.aplikasis.fittrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aplikasis.fittrack.ui.screen.DashboardAdminScreen
import com.aplikasis.fittrack.ui.screen.DataPenggunaScreen
import com.aplikasis.fittrack.ui.screen.FormKontenScreen
import com.aplikasis.fittrack.ui.screen.KelolaKontenScreen
import com.aplikasis.fittrack.ui.screen.KelolaVideoScreen
import com.aplikasis.fittrack.ui.screen.LoginScreen
import com.aplikasis.fittrack.ui.screen.PersonalizationScreen
import com.aplikasis.fittrack.ui.screen.RegisterScreen
import com.aplikasis.fittrack.ui.screen.WelcomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
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
                    if (user.role == "admin") {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate(Screen.Personalization.route) {
                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Personalization.route) {
            PersonalizationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateProgramClick = {
                    // Nanti lanjut ke dashboard user
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            DashboardAdminScreen(
                onKelolaKontenClick = {
                    navController.navigate(Screen.KelolaKonten.route)
                },
                onKelolaVideoClick = {
                    navController.navigate(Screen.KelolaVideo.route)
                },
                onDataPenggunaClick = {
                    navController.navigate(Screen.DataPengguna.route)
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AdminDashboard.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.KelolaKonten.route) {
            KelolaKontenScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTambahClick = {
                    navController.navigate(Screen.FormKonten.createRoute(0L))
                },
                onEditClick = { idKonten ->
                    navController.navigate(Screen.FormKonten.createRoute(idKonten))
                }
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
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.KelolaVideo.route) {
            KelolaVideoScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.DataPengguna.route) {
            DataPenggunaScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}