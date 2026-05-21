package com.aplikasis.fittrack.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aplikasis.fittrack.data.database.FitTrackDatabase

@Composable
fun rememberAdminViewModel(): AdminViewModel {
    val context = LocalContext.current
    val dao = remember {
        FitTrackDatabase.getDatabase(context).fitTrackDao()
    }

    return viewModel(
        factory = AdminViewModelFactory(dao)
    )
}