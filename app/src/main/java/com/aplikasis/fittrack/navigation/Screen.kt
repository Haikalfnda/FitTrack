package com.aplikasis.fittrack.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Personalization : Screen("personalization")

    object AdminDashboard : Screen("admin_dashboard")
    object KelolaKonten : Screen("kelola_konten")
    object KelolaVideo : Screen("kelola_video")
    object DataPengguna : Screen("data_pengguna")

    object FormKonten : Screen("form_konten/{idKonten}") {
        fun createRoute(idKonten: Long = 0L): String {
            return "form_konten/$idKonten"
        }
    }
}