package com.aplikasis.fittrack.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aplikasis.fittrack.data.dao.FitTrackDao
import com.aplikasis.fittrack.data.entity.KontenEntity
import com.aplikasis.fittrack.data.entity.RiwayatLatihanEntity
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity

/**
 * Perubahan versi:
 * v6 → v7: Tambah kolom [is_personalized] (INTEGER DEFAULT 0) di tabel users.
 *
 * PENTING: fallbackToDestructiveMigration() DIHAPUS dan diganti migrasi eksplisit
 * agar data user yang sudah ada tidak hilang.
 */
@Database(
    entities = [
        UserEntity::class,
        KontenEntity::class,
        VideoTutorialEntity::class,
        RiwayatLatihanEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class FitTrackDatabase : RoomDatabase() {

    abstract fun fitTrackDao(): FitTrackDao

    companion object {
        @Volatile
        private var INSTANCE: FitTrackDatabase? = null

        /**
         * Migrasi v6 → v7:
         * - Tambah kolom is_personalized ke tabel users (default 0 = belum personalisasi)
         * - User lama yang sudah aktif dan sudah pakai aplikasi, status isPersonalized
         *   dibiarkan 0 sehingga akan diminta personalisasi sekali lagi (aman).
         *   Jika ingin semua user lama dianggap sudah personalisasi, ganti DEFAULT 0 → DEFAULT 1.
         */
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE users ADD COLUMN is_personalized INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        fun getDatabase(context: Context): FitTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitTrackDatabase::class.java,
                    "fittrack_database"
                )
                    .addMigrations(MIGRATION_6_7)
                    // fallbackToDestructiveMigration() dihapus — data tidak boleh hilang
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}