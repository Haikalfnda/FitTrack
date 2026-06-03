package com.aplikasis.fittrack.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplikasis.fittrack.data.dao.FitTrackDao
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class VideoTutorialViewModel(
    dao: FitTrackDao
) : ViewModel() {

    val daftarVideo: StateFlow<List<VideoTutorialEntity>> =
        dao.getAllVideo()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
}