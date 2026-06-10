package com.aplikasis.fittrack.model

enum class TipeGerakan {
    REPS,
    DURASI
}

data class GerakanWorkout(
    val nama: String,
    val kaloriPerRep: Double,
    val set: Int,
    val repTarget: Int,
    val repSelesai: Int = 0,
    val istirahat: String? = null,
    val catatan: String? = null,
    val isSedang: Boolean = false,
    val tipe: TipeGerakan = TipeGerakan.REPS
) {

    val targetPerSet: Int
        get() = if (set > 0) repTarget / set else repTarget

    val unitLabel: String
        get() = if (tipe == TipeGerakan.DURASI) "detik" else "reps"

    val kaloriTerbakar: Double get() = kaloriPerRep * repSelesai

    val kaloriTerbakarLabel: String get() = "%.2f kcal".format(kaloriTerbakar)
}


object KaloriPerRep {
    const val PUSH_UP          = 0.40
    const val SQUAT            = 0.32
    const val SIT_UP           = 0.25
    const val JUMPING_JACK     = 0.20
    const val MOUNTAIN_CLIMBER = 0.15
    const val SHOULDER_TAP     = 0.18
    const val PLANK            = 0.10
    const val WALL_SIT         = 0.09
    const val LUNGE            = 0.28
    const val BURPEE           = 0.50
    const val HIGH_KNEES       = 0.12


    fun dari(namaGerakan: String): Double = when {
        namaGerakan.contains("push up", ignoreCase = true)          -> PUSH_UP
        namaGerakan.contains("squat", ignoreCase = true)            -> SQUAT
        namaGerakan.contains("sit up", ignoreCase = true)           -> SIT_UP
        namaGerakan.contains("jumping jack", ignoreCase = true)     -> JUMPING_JACK
        namaGerakan.contains("mountain climber", ignoreCase = true) -> MOUNTAIN_CLIMBER
        namaGerakan.contains("shoulder tap", ignoreCase = true)     -> SHOULDER_TAP
        namaGerakan.contains("plank", ignoreCase = true)            -> PLANK
        namaGerakan.contains("wall sit", ignoreCase = true)         -> WALL_SIT
        namaGerakan.contains("lunge", ignoreCase = true)            -> LUNGE
        namaGerakan.contains("burpee", ignoreCase = true)           -> BURPEE
        namaGerakan.contains("high knees", ignoreCase = true)       -> HIGH_KNEES
        else                                                         -> 0.20  // default
    }
}
