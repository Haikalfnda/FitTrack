package com.aplikasis.fittrack.navigation


sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Personalization : Screen("personalization")
}