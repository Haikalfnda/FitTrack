package com.aplikasis.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.aplikasis.fittrack.navigation.NavGraph
import com.aplikasis.fittrack.ui.theme.PrimaryBlue
import com.aplikasis.fittrack.ui.theme.ScreenBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = PrimaryBlue,
                    background = ScreenBg
                )
            ) {
                FitTrackApp()
            }
        }
    }
}

@Composable
fun FitTrackApp() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
