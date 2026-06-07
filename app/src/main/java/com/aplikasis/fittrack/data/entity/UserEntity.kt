package com.aplikasis.fittrack.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Perubahan dari versi sebelumnya:
 * 1. Field [isPersonalized] ditambahkan → Fitur 4 (skip personalisasi)
 * 2. Field [status] sekarang mendukung nilai: "pending", "aktif", "rejected", "nonaktif"
 *    - "pending"  → menunggu persetujuan admin (Fitur 2)
 *    - "aktif"    → sudah disetujui, bisa login
 *    - "rejected" → ditolak admin (Fitur 2)
 *    - "nonaktif" → dinonaktifkan admin (existing)
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_user")
    val idUser: Long = 0,

    val nama: String,
    val email: String,
    val password: String,

    @ColumnInfo(name = "tanggal_lahir")
    val tanggalLahir: String = "",

    val level: String = "",
    val tujuan: String = "",

    @ColumnInfo(name = "durasi_latihan")
    val durasiLatihan: String = "",

    @ColumnInfo(name = "target_hari_per_minggu")
    val targetHariPerMinggu: Int = 0,

    val role: String = "user",

    /**
     * Status akun:
     * - "pending"  → baru register, menunggu persetujuan admin
     * - "aktif"    → disetujui admin, dapat mengakses aplikasi
     * - "rejected" → ditolak admin
     * - "nonaktif" → dinonaktifkan admin (toggle existing)
     */
    val status: String = "pending",

    /**
     * Fitur 4: Flag apakah user sudah menyelesaikan personalisasi.
     * Default false → belum personalisasi.
     * Di-set true setelah klik "Buat Program Saya" di PersonalizationScreen.
     */
    @ColumnInfo(name = "is_personalized", defaultValue = "0")
    val isPersonalized: Boolean = false
)