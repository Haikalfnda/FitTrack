package com.aplikasis.fittrack.model

/**
 * FITUR 5: Data yang dibawa dari DetailProgramScreen ke RingkasanSesiScreen
 * via NavGraph setelah user klik "Selesai Sesi Ini".
 *
 * Berisi:
 * - Total repetisi yang benar-benar diselesaikan
 * - Total kalori terbakar (akurat meski sesi tidak selesai penuh)
 * - Detail per gerakan untuk ditampilkan di ringkasan
 */
data class HasilSesi(
    val namaProgram: String = "Full Body Beginner",
    val hariLatihan: String = "Sesi hari ini",
    val fokusLatihan: String = "Full body",
    val level: String = "Pemula",
    val totalRep: Int,
    val totalDurasiDetik: Int = 0,
    val totalKalori: Double,
    val detailGerakan: List<DetailGerakan>
) {
    val durasiLabel: String
        get() = when {
            totalDurasiDetik <= 0 -> "0 detik"
            totalDurasiDetik < 60 -> "$totalDurasiDetik detik"
            totalDurasiDetik % 60 == 0 -> "${totalDurasiDetik / 60} menit"
            else -> "${totalDurasiDetik / 60} menit ${totalDurasiDetik % 60} detik"
        }
}

data class DetailGerakan(
    val nama: String,
    val repSelesai: Int,
    val repTarget: Int,
    val kalori: Double,
    val setSelesai: List<Int> = emptyList(),
    val targetPerSet: Int = repTarget,
    val jumlahSet: Int = if (setSelesai.isNotEmpty()) setSelesai.size else 1,
    val tipe: TipeGerakan = TipeGerakan.REPS
) {
    val kaloriLabel: String get() = "%.2f kcal".format(kalori)

    val unitLabel: String
        get() = if (tipe == TipeGerakan.DURASI) "detik" else "reps"

    val setLabel: String
        get() = setSelesai.mapIndexed { index, nilai ->
            "Set ${index + 1}: $nilai $unitLabel"
        }.joinToString(", ")

    val progressLabel: String
        get() = "$repSelesai/$repTarget $unitLabel"
}
