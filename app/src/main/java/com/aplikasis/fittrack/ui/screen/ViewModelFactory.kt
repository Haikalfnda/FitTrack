package com.aplikasis.fittrack.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aplikasis.fittrack.data.dao.FitTrackDao

class ViewModelFactory(private val dao: FitTrackDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RiwayatLatihanViewModel::class.java)) {
            return RiwayatLatihanViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}