// ui/screen/RiwayatLatihanViewModel.kt
package com.aplikasis.fittrack.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplikasis.fittrack.data.dao.FitTrackDao
import com.aplikasis.fittrack.data.entity.RiwayatLatihanEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RiwayatLatihanViewModel(private val dao: FitTrackDao) : ViewModel() {

    // Menyimpan state filter saat ini
    val selectedFilter = MutableStateFlow("Harian")

    // Mengambil data secara dinamis dari database setiap kali filter berubah
    @OptIn(ExperimentalCoroutinesApi::class)
    val dataRiwayatState: StateFlow<List<RiwayatLatihanEntity>> = selectedFilter
        .flatMapLatest { filter ->
            dao.getRiwayatByFilter(filter)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun changeFilter(filter: String) {
        selectedFilter.value = filter
    }

    // Fungsi pembantu jika Anda ingin mencoba menyisipkan data tiruan (dummy) ke DB untuk testing
    fun insertDummyData(riwayat: RiwayatLatihanEntity) {
        viewModelScope.launch {
            dao.insertRiwayat(riwayat)
        }
    }
}