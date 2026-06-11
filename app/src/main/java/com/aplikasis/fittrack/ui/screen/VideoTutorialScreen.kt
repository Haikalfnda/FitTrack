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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    var videoDiputarId by remember {
        mutableStateOf<Long?>(null)
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
        if (!initialGerakan.isNullOrBlank() && daftarVideo.isNotEmpty() && videoDiputarId == null) {
            val kataKunci = initialGerakan.trim()
            val videoGerakan = daftarVideo.firstOrNull { video ->
                video.judul.contains(kataKunci, ignoreCase = true) ||
                    video.deskripsi.contains(kataKunci, ignoreCase = true) ||
                    video.kategori.contains(kataKunci, ignoreCase = true)
            } ?: daftarVideo.first()

            kategoriDipilih = "Semua"
            videoDiputarId = videoGerakan.idVideo
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
                    val sedangDiputar = videoDiputarId == video.idVideo

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        VideoUserCard(
                            video = video,
                            isPlaying = sedangDiputar,
                            onClick = {
                                videoDiputarId = if (sedangDiputar) null else video.idVideo
                            }
                        )

                        if (sedangDiputar) {
                            InlineVideoPlayerCard(
                                video = video,
                                onOpenDetail = { onVideoClick(video) }
                            )
                        }
                    }
                }
            }
        }
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
private fun InlineVideoPlayerCard(
    video: VideoTutorialEntity,
    onOpenDetail: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.Black)
            ) {
                EmbeddedVideoPlayer(
                    url = video.videoUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.judul,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        maxLines = 1
                    )
                    Text(
                        text = "Player terbuka langsung di aplikasi",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                TextButton(onClick = onOpenDetail) {
                    Text(
                        text = "Detail",
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold
                    )
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
