package com.aplikasis.fittrack.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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

    // Menyimpan pilihan level (Pemula / Menengah / Lanjutan)
    val level: String = "",

    // Menyimpan pilihan tujuan (Turun berat badan / dll)
    val tujuan: String = "",

    @ColumnInfo(name = "durasi_latihan")
    val durasiLatihan: String = "",

    @ColumnInfo(name = "target_hari_per_minggu")
    val targetHariPerMinggu: Int = 0,

    val role: String = "user",
    val status: String = "aktif"
)