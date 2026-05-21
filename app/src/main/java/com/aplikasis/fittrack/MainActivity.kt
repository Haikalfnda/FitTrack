package com.aplikasis.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.aplikasis.fittrack.data.database.FitTrackDatabase
import com.aplikasis.fittrack.data.entity.UserEntity
import com.aplikasis.fittrack.navigation.NavGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = FitTrackDatabase.getDatabase(this)
        val dao = database.fitTrackDao()

        setContent {

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    if (dao.countAdmin() == 0) {
                        dao.insertUser(
                            UserEntity(
                                nama = "Admin",
                                email = "admin@fittrack.com",
                                password = "admin123",
                                tanggalLahir = "",
                                level = "",
                                tujuan = "",
                                role = "admin",
                                status = "aktif"
                            )
                        )
                    }
                }
            }

            MaterialTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}