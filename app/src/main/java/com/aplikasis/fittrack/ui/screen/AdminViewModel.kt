package com.aplikasis.fittrack.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aplikasis.fittrack.data.dao.FitTrackDao
import com.aplikasis.fittrack.data.entity.KontenEntity
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val dao: FitTrackDao
) : ViewModel() {

    val jumlahUserAktif = dao.countUserAktif().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    val jumlahKonten = dao.countKonten().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    val jumlahVideo = dao.countVideo().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    val daftarKonten = dao.getAllKonten().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val daftarVideo = dao.getAllVideo().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val daftarUser = dao.getAllUsers().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )


    // KONTEN
    fun tambahKonten(konten: KontenEntity) {
        viewModelScope.launch {
            dao.insertKonten(konten)
        }
    }

    fun updateKonten(konten: KontenEntity) {
        viewModelScope.launch {
            dao.updateKonten(konten)
        }
    }

    fun hapusKonten(konten: KontenEntity) {
        viewModelScope.launch {
            dao.deleteKonten(konten)
        }
    }


    // VIDEO
    fun tambahVideo(video: VideoTutorialEntity) {
        viewModelScope.launch {
            dao.insertVideo(video)
        }
    }

    fun updateVideo(video: VideoTutorialEntity) {
        viewModelScope.launch {
            dao.updateVideo(video)
        }
    }

    fun hapusVideo(video: VideoTutorialEntity) {
        viewModelScope.launch {
            dao.deleteVideo(video)
        }
    }


    // USER
    fun ubahStatusUser(user: UserEntity) {
        val statusBaru = if (user.status.equals("aktif", ignoreCase = true)) {
            "nonaktif"
        } else {
            "aktif"
        }

        viewModelScope.launch {
            dao.updateUserStatus(user.idUser, statusBaru)
        }
    }
}

class AdminViewModelFactory(
    private val dao: FitTrackDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(dao) as T
        }
        throw IllegalArgumentException("ViewModel tidak ditemukan")
    }
}