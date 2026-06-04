package com.aplikasis.fittrack.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_tutorial")
data class VideoTutorialEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_video")
    val idVideo: Long = 0,

    val judul: String,
    val kategori: String,
    val deskripsi: String,

    @ColumnInfo(name = "video_url")
    val videoUrl: String,

)