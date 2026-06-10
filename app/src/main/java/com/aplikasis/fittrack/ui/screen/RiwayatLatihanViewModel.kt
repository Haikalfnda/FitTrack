// ui/screen/RiwayatLatihanViewModel.kt
package com.aplikasis.fittrack.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplikasis.fittrack.data.dao.FitTrackDao
import com.aplikasis.fittrack.data.entity.RiwayatLatihanEntity
import com.aplikasis.fittrack.model.LatihanStats
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RiwayatLatihanViewModel(
    private val dao: FitTrackDao,
    private val idUser: Long
) : ViewModel() {

    // Menyimpan state filter saat ini
    val selectedFilter = MutableStateFlow("Mingguan")

    private val semuaRiwayatUser = dao.getRiwayatByUser(idUser)

    // Mengambil data user aktif secara real-time, lalu difilter di aplikasi.
    @OptIn(ExperimentalCoroutinesApi::class)
    val dataRiwayatState: StateFlow<List<RiwayatLatihanEntity>> = selectedFilter.flatMapLatest { filter ->
        semuaRiwayatUser.map { riwayat ->
            LatihanStats.filterRiwayat(riwayat, filter)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun changeFilter(filter: String) {
        selectedFilter.value = filter
    }

    fun insertRiwayat(riwayat: RiwayatLatihanEntity) {
        viewModelScope.launch {
            dao.insertRiwayat(riwayat)
        }
    }
}
