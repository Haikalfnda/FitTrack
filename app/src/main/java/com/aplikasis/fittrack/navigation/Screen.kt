package com.aplikasis.fittrack.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Personalization : Screen("personalization")

    // ── User screens ──────────────────────────────────────────────────────────
    object Beranda          : Screen("beranda")
    object ProgramLatihan   : Screen("program_latihan")
    object DetailProgram    : Screen("detail_program")
    object RingkasanSesi    : Screen("ringkasan_sesi")
    object ProgressTracking : Screen("progress_tracking")
    object RiwayatLatihan   : Screen("riwayat_latihan")
    object VideoTutorial : Screen("video_tutorial") {
        const val argGerakan = "gerakan"
        const val routeWithGerakan = "video_tutorial?gerakan={gerakan}"

        fun createRoute(gerakan: String? = null): String {
            val gerakanAman = gerakan?.takeIf { it.isNotBlank() } ?: return route
            return "video_tutorial?gerakan=${Uri.encode(gerakanAman)}"
        }
    }
    object DetailArtikel : Screen("detail_artikel/{idKonten}") {
        fun createRoute(idKonten: Long) =
            "detail_artikel/$idKonten"
    }
    object DetailVideo : Screen("detail_video/{videoId}") {
        fun createRoute(videoId: Long) =
            "detail_video/$videoId"
    }

    // ── Admin screens ─────────────────────────────────────────────────────────
    object AdminDashboard : Screen("admin_dashboard")
    object KelolaKonten   : Screen("kelola_konten")
    object KelolaVideo    : Screen("kelola_video")
    object DataPengguna   : Screen("data_pengguna")

    object FormKonten : Screen("form_konten/{idKonten}") {
        fun createRoute(idKonten: Long = 0L): String {
            return "form_konten/$idKonten"
        }
    }
}
