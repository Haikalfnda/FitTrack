package com.aplikasis.fittrack.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aplikasis.fittrack.data.dao.FitTrackDao

class ViewModelFactory(
    private val dao: FitTrackDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        println("DEBUG VM = ${modelClass.name}")

        return when {

            modelClass.isAssignableFrom(
                RiwayatLatihanViewModel::class.java
            ) -> {
                RiwayatLatihanViewModel(dao) as T
            }

            modelClass.isAssignableFrom(
                VideoTutorialViewModel::class.java
            ) -> {
                VideoTutorialViewModel(dao) as T
            }

            modelClass.isAssignableFrom(
                VideoDetailViewModel::class.java
            ) -> {
                VideoDetailViewModel(dao) as T
            }

            else -> {
                throw IllegalArgumentException(
                    "Unknown ViewModel class: ${modelClass.name}"
                )
            }
        }
    }
}