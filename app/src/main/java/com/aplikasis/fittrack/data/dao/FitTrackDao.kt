package com.aplikasis.fittrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aplikasis.fittrack.data.entity.KontenEntity
import com.aplikasis.fittrack.data.entity.RiwayatLatihanEntity
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FitTrackDao {

    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE role = 'admin'")
    suspend fun countAdmin(): Int

    @Query("SELECT * FROM users WHERE role = 'user'")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("UPDATE users SET status = :status WHERE id_user = :idUser")
    suspend fun updateUserStatus(idUser: Long, status: String)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Insert
    suspend fun insertKonten(konten: KontenEntity): Long

    @Update
    suspend fun updateKonten(konten: KontenEntity)

    @Delete
    suspend fun deleteKonten(konten: KontenEntity)

    @Query("SELECT * FROM konten ORDER BY id_konten DESC")
    fun getAllKonten(): Flow<List<KontenEntity>>

    @Query("SELECT * FROM konten WHERE id_konten = :idKonten LIMIT 1")
    suspend fun getKontenById(idKonten: Long): KontenEntity?

    @Query("SELECT COUNT(*) FROM konten")
    fun countKonten(): Flow<Int>

    @Insert
    suspend fun insertVideo(video: VideoTutorialEntity): Long

    @Update
    suspend fun updateVideo(video: VideoTutorialEntity)

    @Delete
    suspend fun deleteVideo(video: VideoTutorialEntity)

    @Query("SELECT * FROM video_tutorial ORDER BY id_video DESC")
    fun getAllVideo(): Flow<List<VideoTutorialEntity>>

    @Query("""
    SELECT * FROM video_tutorial
    WHERE (:kategori = 'Semua' OR kategori = :kategori)
    ORDER BY id_video DESC""")
    fun getVideoByKategori(kategori: String): Flow<List<VideoTutorialEntity>>

    @Query("""SELECT * FROM video_tutorial WHERE id_video = :idVideo LIMIT 1""")
    suspend fun getVideoById(
        idVideo: Long
    ): VideoTutorialEntity?

    @Query("SELECT COUNT(*) FROM video_tutorial")
    fun countVideo(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'user' AND status = 'aktif'")
    fun countUserAktif(): Flow<Int>

    // Mengambil data berdasarkan filter secara otomatis & real-time
    @Query("SELECT * FROM riwayat_latihan WHERE tipeFilter = :filter ORDER BY id DESC")
    fun getRiwayatByFilter(filter: String): Flow<List<RiwayatLatihanEntity>>

    // Dipanggil di akhir sesi latihan user untuk menyimpan data ke database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayat(riwayat: RiwayatLatihanEntity)

    @Query("SELECT * FROM users WHERE id_user = :idUser LIMIT 1")
    fun getUserById(idUser: Long): Flow<UserEntity?>

    // 2. Menghitung jumlah latihan mingguan yang sudah selesai
    @Query("SELECT COUNT(*) FROM riwayat_latihan WHERE tipeFilter = 'Mingguan' AND idUser = :idUser")
    fun countLatihanMingguIni(idUser: Long): Flow<Int>

    // 3. Mengupdate data preferensi program ketika klik "Buat program saya"
    @Query("""
        UPDATE users 
        SET level = :level, tujuan = :tujuan, durasi_latihan = :durasi, target_hari_per_minggu = :targetHari 
        WHERE id_user = :idUser
    """)
    suspend fun updatePersonalisasiUser(idUser: Long, level: String, tujuan: String, durasi: String, targetHari: Int)
}