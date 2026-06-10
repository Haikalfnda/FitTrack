package com.aplikasis.fittrack.model

import com.aplikasis.fittrack.data.entity.RiwayatLatihanEntity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

data class RingkasanLatihan(
    val totalReps: Int = 0,
    val totalKalori: Int = 0,
    val totalDurasiMenit: Int = 0
) {
    val durasiLabel: String
        get() = when {
            totalDurasiMenit <= 0 -> "0 menit"
            totalDurasiMenit < 60 -> "$totalDurasiMenit menit"
            totalDurasiMenit % 60 == 0 -> "${totalDurasiMenit / 60} jam"
            else -> "${totalDurasiMenit / 60}j ${totalDurasiMenit % 60}m"
        }
}

data class ProgressLatihan(
    val trenRep: List<Int> = List(6) { 0 },
    val persenNaik: Int = 0,
    val rataRataReps: Int = 0,
    val diffVsMingguLalu: Int = 0,
    val kaloriTerbakar: Int = 0,
    val statusKalori: String = "Belum ada data",
    val frekuensiPerMinggu: Int = 0,
    val statusFrekuensi: String = "Belum ada latihan",
    val hasData: Boolean = false
)

object LatihanStats {
    private val localeId = Locale("id", "ID")
    private val formatterId = DateTimeFormatter.ofPattern("dd MMM yyyy", localeId)
    private val formatterEn = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    private val weekFields = WeekFields.of(localeId)
    private val angkaRegex = Regex("""\d+(?:[.,]\d+)?""")

    fun formatTanggal(date: LocalDate = LocalDate.now()): String = date.format(formatterId)

    fun parseTanggal(tanggal: String): LocalDate? {
        return listOf(formatterId, formatterEn).firstNotNullOfOrNull { formatter ->
            runCatching { LocalDate.parse(tanggal, formatter) }.getOrNull()
        }
    }

    fun hitungRingkasan(riwayat: List<RiwayatLatihanEntity>): RingkasanLatihan {
        return RingkasanLatihan(
            totalReps = riwayat.sumOf { ambilReps(it) },
            totalKalori = riwayat.sumOf { ambilKalori(it) }.roundToInt(),
            totalDurasiMenit = riwayat.sumOf { ambilDurasiMenit(it) }
        )
    }

    fun filterRiwayat(
        riwayat: List<RiwayatLatihanEntity>,
        filter: String,
        today: LocalDate = LocalDate.now()
    ): List<RiwayatLatihanEntity> {
        return riwayat.filter { item ->
            val tanggal = parseTanggal(item.tanggal) ?: return@filter false
            when (filter) {
                "Harian" -> tanggal == today
                "Mingguan" -> isSameWeek(tanggal, today)
                "Bulanan" -> YearMonth.from(tanggal) == YearMonth.from(today)
                else -> true
            }
        }
    }

    fun hitungHariLatihanMingguIni(
        riwayat: List<RiwayatLatihanEntity>,
        today: LocalDate = LocalDate.now()
    ): Int {
        return riwayat
            .mapNotNull { parseTanggal(it.tanggal) }
            .filter { isSameWeek(it, today) }
            .toSet()
            .size
    }

    fun hitungStreakSaatIni(
        riwayat: List<RiwayatLatihanEntity>,
        today: LocalDate = LocalDate.now()
    ): Int {
        val tanggalLatihan = riwayat.mapNotNull { parseTanggal(it.tanggal) }.toSet()
        if (tanggalLatihan.isEmpty()) return 0

        var cursor = when {
            tanggalLatihan.contains(today) -> today
            tanggalLatihan.contains(today.minusDays(1)) -> today.minusDays(1)
            else -> return 0
        }

        var streak = 0
        while (tanggalLatihan.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    fun hitungProgress(
        riwayat: List<RiwayatLatihanEntity>,
        targetHariPerMinggu: Int,
        today: LocalDate = LocalDate.now()
    ): ProgressLatihan {
        val trenRep = (5 downTo 0).map { offset ->
            val minggu = today.minusWeeks(offset.toLong())
            riwayat.filter { item ->
                parseTanggal(item.tanggal)?.let { isSameWeek(it, minggu) } ?: false
            }.sumOf { ambilReps(it) }
        }

        val mingguIni = riwayat.filter { item ->
            parseTanggal(item.tanggal)?.let { isSameWeek(it, today) } ?: false
        }
        val mingguLalu = riwayat.filter { item ->
            parseTanggal(item.tanggal)?.let { isSameWeek(it, today.minusWeeks(1)) } ?: false
        }

        val avgIni = rataRataReps(mingguIni)
        val avgLalu = rataRataReps(mingguLalu)
        val kaloriIni = mingguIni.sumOf { ambilKalori(it) }.roundToInt()
        val kaloriLalu = mingguLalu.sumOf { ambilKalori(it) }.roundToInt()
        val frekuensi = mingguIni.mapNotNull { parseTanggal(it.tanggal) }.toSet().size
        val baseline = trenRep.firstOrNull { it > 0 } ?: 0
        val terakhir = trenRep.lastOrNull() ?: 0
        val persenNaik = if (baseline > 0) {
            (((terakhir - baseline).toDouble() / baseline.toDouble()) * 100).roundToInt()
        } else {
            0
        }

        return ProgressLatihan(
            trenRep = trenRep,
            persenNaik = persenNaik,
            rataRataReps = avgIni,
            diffVsMingguLalu = avgIni - avgLalu,
            kaloriTerbakar = kaloriIni,
            statusKalori = statusPerbandingan(kaloriIni, kaloriLalu),
            frekuensiPerMinggu = frekuensi,
            statusFrekuensi = statusFrekuensi(frekuensi, targetHariPerMinggu),
            hasData = riwayat.isNotEmpty()
        )
    }

    fun ambilReps(item: RiwayatLatihanEntity): Int {
        return angkaPertama(item.reps)?.roundToInt() ?: 0
    }

    private fun ambilKalori(item: RiwayatLatihanEntity): Double {
        return angkaPertama(item.kalori) ?: 0.0
    }

    private fun ambilDurasiMenit(item: RiwayatLatihanEntity): Int {
        val angka = angkaPertama(item.durasi) ?: return 0
        return when {
            item.durasi.contains("jam", ignoreCase = true) -> (angka * 60).roundToInt()
            item.durasi.contains("detik", ignoreCase = true) -> ceil(angka / 60.0).toInt()
            else -> angka.roundToInt()
        }
    }

    private fun angkaPertama(text: String): Double? {
        return angkaRegex.find(text)
            ?.value
            ?.replace(',', '.')
            ?.toDoubleOrNull()
    }

    private fun rataRataReps(items: List<RiwayatLatihanEntity>): Int {
        if (items.isEmpty()) return 0
        return items.map { ambilReps(it) }.average().roundToInt()
    }

    private fun statusPerbandingan(nilaiIni: Int, nilaiLalu: Int): String {
        if (nilaiIni == 0 && nilaiLalu == 0) return "Belum ada data"
        val diff = nilaiIni - nilaiLalu
        return when {
            diff > 20 -> "Naik"
            diff < -20 -> "Turun"
            else -> "Stabil"
        }
    }

    private fun statusFrekuensi(frekuensi: Int, targetHariPerMinggu: Int): String {
        val target = targetHariPerMinggu.coerceAtLeast(1)
        return when {
            frekuensi <= 0 -> "Belum ada latihan"
            frekuensi >= target -> "Target tercapai"
            frekuensi >= max(1, target / 2) -> "Konsisten"
            else -> "Mulai terbentuk"
        }
    }

    private fun isSameWeek(date: LocalDate, anchor: LocalDate): Boolean {
        return date.get(weekFields.weekBasedYear()) == anchor.get(weekFields.weekBasedYear()) &&
            date.get(weekFields.weekOfWeekBasedYear()) == anchor.get(weekFields.weekOfWeekBasedYear())
    }
}
