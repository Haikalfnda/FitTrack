package com.aplikasis.fittrack.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "konten")
data class KontenEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_konten")
    val idKonten: Long = 0,

    val judul: String,

    val kategori: String,

    val isi: String,

    val status: String = "Nonaktif"
)
