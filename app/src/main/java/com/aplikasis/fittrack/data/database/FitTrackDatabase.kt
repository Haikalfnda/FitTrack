package com.aplikasis.fittrack.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aplikasis.fittrack.data.dao.FitTrackDao
import com.aplikasis.fittrack.data.entity.KontenEntity
import com.aplikasis.fittrack.data.entity.RiwayatLatihanEntity
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity

@Database(
    entities = [
        UserEntity::class,
        KontenEntity::class,
        VideoTutorialEntity::class,
        RiwayatLatihanEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class FitTrackDatabase : RoomDatabase() {

    abstract fun fitTrackDao(): FitTrackDao

    companion object {
        @Volatile
        private var INSTANCE: FitTrackDatabase? = null

        fun getDatabase(context: Context): FitTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitTrackDatabase::class.java,
                    "fittrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}