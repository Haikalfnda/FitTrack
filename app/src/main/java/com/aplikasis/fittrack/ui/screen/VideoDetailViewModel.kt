package com.aplikasis.fittrack.ui.screen

import androidx.lifecycle.ViewModel
import com.aplikasis.fittrack.data.dao.FitTrackDao
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity

class VideoDetailViewModel(
    private val dao: FitTrackDao
) : ViewModel() {

    suspend fun getVideo(
        id: Long
    ): VideoTutorialEntity? {

        return dao.getVideoById(id)
    }
}