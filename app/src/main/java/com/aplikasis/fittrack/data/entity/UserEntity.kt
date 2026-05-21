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

    val level: String = "",
    val tujuan: String = "",

    val role: String = "user",

    val status: String = "aktif"
)