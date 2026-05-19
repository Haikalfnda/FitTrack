package com.aplikasis.fittrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
                    // Nanti lanjut ke dashboard
                }
            )
        }
    }
}