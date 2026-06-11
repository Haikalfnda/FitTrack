package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity
import com.aplikasis.fittrack.utils.YoutubeUtils.getYoutubeThumbnail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoTutorialScreen(
    viewModel: VideoTutorialViewModel,
    initialGerakan: String? = null,
    onBackClick: () -> Unit,
    onVideoClick: (VideoTutorialEntity) -> Unit
) {

    val daftarVideo by viewModel.daftarVideo.collectAsState()

    var kategoriDipilih by remember {
        mutableStateOf("Semua")
    }
    var videoDiputar by remember {
        mutableStateOf<VideoTutorialEntity?>(null)
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

    LaunchedEffect(initialGerakan, daftarVideo) {
        if (!initialGerakan.isNullOrBlank() && daftarVideo.isNotEmpty() && videoDiputar == null) {
            val kataKunci = initialGerakan.trim()
            val videoGerakan = daftarVideo.firstOrNull { video ->
                video.judul.contains(kataKunci, ignoreCase = true) ||
                    video.deskripsi.contains(kataKunci, ignoreCase = true) ||
                    video.kategori.contains(kataKunci, ignoreCase = true)
            } ?: daftarVideo.first()

            kategoriDipilih = "Semua"
            videoDiputar = videoGerakan
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Video Tutorial",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Text("←")
                    }
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
                if (videoFiltered.isEmpty()) {
                    item {
                        EmptyVideoTutorialCard()
                    }
                }

                items(
                    items = videoFiltered,
                    key = { it.idVideo }
                ) { video ->
                    val sedangDiputar = videoDiputar?.idVideo == video.idVideo

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        VideoUserCard(
                            video = video,
                            isPlaying = sedangDiputar,
                            onClick = {
                                videoDiputar = if (sedangDiputar) null else video
                            }
                        )
                    }
                }
            }
        }
    }

    videoDiputar?.let { video ->
        VideoPlayerDialog(
            video = video,
            onDismiss = { videoDiputar = null },
            onOpenDetail = {
                videoDiputar = null
                onVideoClick(video)
            }
        )
    }
}
@Composable
private fun VideoUserCard(
    video: VideoTutorialEntity,
    isPlaying: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) Color(0xFFEAF2FF) else Color.White
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            AsyncImage(
                model = getYoutubeThumbnail(video.videoUrl),
                contentDescription = video.judul,
                modifier = Modifier
                    .size(
                        width = 100.dp,
                        height = 80.dp
                    )
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = video.judul,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Video Tutorial",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isPlaying) "Sedang diputar di aplikasi" else "Putar di aplikasi",
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun VideoPlayerDialog(
    video: VideoTutorialEntity,
    onDismiss: () -> Unit,
    onOpenDetail: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.86f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220))
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .background(Color.Black)
                    ) {
                        EmbeddedVideoPlayer(
                            url = video.videoUrl,
                            modifier = Modifier.fillMaxSize()
                        )

                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.58f),
                                    shape = RoundedCornerShape(50)
                                )
                        ) {
                            Text(
                                text = "Tutup",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = video.judul,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Video diputar langsung di aplikasi FitTrack.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.72f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onOpenDetail) {
                                Text(
                                    text = "Detail",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyVideoTutorialCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = "Belum ada video tutorial",
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )
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
