package com.aplikasis.fittrack.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoTutorialScreen(
    viewModel: VideoTutorialViewModel,
    onBackClick: () -> Unit
) {

    val daftarVideo by viewModel.daftarVideo.collectAsState()

    var kategoriDipilih by remember {
        mutableStateOf("Semua")
    }

    val videoFiltered = when (kategoriDipilih) {
        "Pemula" ->
            daftarVideo.filter {
                it.kategori.equals("Pemula", true)
            }

        "Kekuatan" ->
            daftarVideo.filter {
                it.kategori.equals("Kekuatan", true)
            }

        else ->
            daftarVideo
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Video Tutorial",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        containerColor = Color(0xFFF6F8FC)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChipVideo(
                    "Semua",
                    kategoriDipilih == "Semua"
                ) {
                    kategoriDipilih = "Semua"
                }

                FilterChipVideo(
                    "Pemula",
                    kategoriDipilih == "Pemula"
                ) {
                    kategoriDipilih = "Pemula"
                }

                FilterChipVideo(
                    "Kekuatan",
                    kategoriDipilih == "Kekuatan"
                ) {
                    kategoriDipilih = "Kekuatan"
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(videoFiltered) { video ->

                    VideoUserCard(video)
                }
            }
        }
    }
}
@Composable
private fun VideoUserCard(
    video: VideoTutorialEntity
) {

    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        Color(0xFFEAF1FF),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "▶",
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    video.judul,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    video.deskripsi
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "Tonton sekarang",
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {

                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(video.videoUrl)
                        )

                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}
@Composable
private fun FilterChipVideo(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (selected)
                    Color(0xFF2563EB)
                else
                    Color(0xFFE8EDF5)
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text,
            color =
                if (selected)
                    Color.White
                else
                    Color.DarkGray
        )
    }
}