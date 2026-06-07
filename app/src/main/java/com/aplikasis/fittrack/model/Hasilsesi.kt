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
    val totalRep: Int,
    val totalKalori: Double,
    val detailGerakan: List<DetailGerakan>
)

data class DetailGerakan(
    val nama: String,
    val repSelesai: Int,
    val repTarget: Int,
    val kalori: Double
) {
    val kaloriLabel: String get() = "%.2f kcal".format(kalori)
}