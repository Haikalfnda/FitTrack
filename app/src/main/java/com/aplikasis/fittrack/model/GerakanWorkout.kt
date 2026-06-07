package com.aplikasis.fittrack.model

/**
 * FITUR 5 - Model Gerakan Workout
 *
 * Menyimpan data satu gerakan beserta:
 * - [kaloriPerRep]: kalori yang terbakar per satu repetisi
 * - [repTarget]: target repetisi untuk sesi ini
 * - [repSelesai]: repetisi yang benar-benar diselesaikan user (diupdate real-time)
 *
 * Kalori dihitung = kaloriPerRep × repSelesai
 * Sehingga jika user berhenti di tengah, kalori tetap akurat.
 */
data class GerakanWorkout(
    val nama: String,
    val kaloriPerRep: Double,   // kalori per 1 repetisi
    val set: Int,
    val repTarget: Int,         // target total repetisi (set × rep per set)
    val repSelesai: Int = 0,    // diisi saat user input / selesai
    val istirahat: String? = null,
    val catatan: String? = null,
    val isSedang: Boolean = false
) {
    /** Kalori aktual = kalori per rep × rep yang benar-benar selesai */
    val kaloriTerbakar: Double get() = kaloriPerRep * repSelesai

    /** Label ringkas untuk tampilan */
    val kaloriTerbakarLabel: String get() = "%.2f kcal".format(kaloriTerbakar)
}

/**
 * Nilai kalori per repetisi untuk setiap jenis gerakan.
 * Tambahkan gerakan baru di sini sesuai kebutuhan.
 */
object KaloriPerRep {
    const val PUSH_UP          = 0.40
    const val SQUAT            = 0.32
    const val SIT_UP           = 0.25
    const val JUMPING_JACK     = 0.20
    const val MOUNTAIN_CLIMBER = 0.15
    const val SHOULDER_TAP     = 0.18
    const val PLANK            = 0.10   // per detik
    const val LUNGE            = 0.28
    const val BURPEE           = 0.50
    const val HIGH_KNEES       = 0.12

    /** Lookup otomatis berdasarkan nama gerakan (case-insensitive). */
    fun dari(namaGerakan: String): Double = when {
        namaGerakan.contains("push up", ignoreCase = true)          -> PUSH_UP
        namaGerakan.contains("squat", ignoreCase = true)            -> SQUAT
        namaGerakan.contains("sit up", ignoreCase = true)           -> SIT_UP
        namaGerakan.contains("jumping jack", ignoreCase = true)     -> JUMPING_JACK
        namaGerakan.contains("mountain climber", ignoreCase = true) -> MOUNTAIN_CLIMBER
        namaGerakan.contains("shoulder tap", ignoreCase = true)     -> SHOULDER_TAP
        namaGerakan.contains("plank", ignoreCase = true)            -> PLANK
        namaGerakan.contains("lunge", ignoreCase = true)            -> LUNGE
        namaGerakan.contains("burpee", ignoreCase = true)           -> BURPEE
        namaGerakan.contains("high knees", ignoreCase = true)       -> HIGH_KNEES
        else                                                         -> 0.20  // default
    }
}