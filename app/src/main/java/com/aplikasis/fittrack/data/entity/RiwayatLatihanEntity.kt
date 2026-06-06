// data/entity/RiwayatLatihanEntity.kt
package com.aplikasis.fittrack.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riwayat_latihan")
data class RiwayatLatihanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idUser: Long,
    val namaProgram: String,
    val tanggal: String,    // Format: "16 Apr 2026" atau bisa menggunakan Timestamp Long
    val durasi: String,     // Contoh: "26 menit"
    val reps: String,       // Contoh: "128 reps"
    val kalori: String,     // Contoh: "214 kcal"
    val detail: String,
    val tipeFilter: String  // Menyimpan kategori: "Harian", "Mingguan", atau "Bulanan"
)