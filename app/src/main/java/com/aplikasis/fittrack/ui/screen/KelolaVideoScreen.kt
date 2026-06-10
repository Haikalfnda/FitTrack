package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity
import com.aplikasis.fittrack.utils.YoutubeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaVideoScreen(
    onBackClick: () -> Unit
) {
    val viewModel = rememberAdminViewModel()
    val daftarVideo by viewModel.daftarVideo.collectAsState()

    var tampilDialog by remember { mutableStateOf(false) }
    var videoEdit by remember { mutableStateOf<VideoTutorialEntity?>(null) }
    var videoHapus by remember { mutableStateOf<VideoTutorialEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kelola Video",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color(0xFF2563EB)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            videoEdit = null
                            tampilDialog = true
                        }
                    ) {
                        Text(text = "+", style = MaterialTheme.typography.headlineSmall)
                    }
                }
            )
        },
        containerColor = Color(0xFFF6F8FC)
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(daftarVideo) { video ->
                VideoCard(
                    video = video,
                    onEditClick = {
                        videoEdit = video
                        tampilDialog = true
                    },
                    onDeleteClick = {
                        videoHapus = video
                    }
                )
            }
        }
    }

    if (tampilDialog) {
        VideoDialog(
            videoAwal = videoEdit,
            onDismiss = {
                tampilDialog = false
                videoEdit = null
            },
            onSave = { video ->
                if (video.idVideo == 0L) {
                    viewModel.tambahVideo(video)
                } else {
                    viewModel.updateVideo(video)
                }

                tampilDialog = false
                videoEdit = null
            }
        )
    }

    if (videoHapus != null) {
        AlertDialog(
            onDismissRequest = { videoHapus = null },
            title = {
                Text(text = "Hapus Video")
            },
            text = {
                Text(text = "Yakin ingin menghapus video ini?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        videoHapus?.let {
                            viewModel.hapusVideo(it)
                        }
                        videoHapus = null
                    }
                ) {
                    Text(text = "Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { videoHapus = null }) {
                    Text(text = "Batal")
                }
            }
        )
    }
}

@Composable
private fun VideoCard(
    video: VideoTutorialEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
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
                    .size(width = 86.dp, height = 70.dp)
                    .background(
                        color = Color(0xFFEAF1FF),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = video.judul,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.kategori,
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.deskripsi,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                Row {
                    TextButton(onClick = onEditClick) {
                        Text(text = "Edit")
                    }

                    TextButton(onClick = onDeleteClick) {
                        Text(text = "Hapus")
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoDialog(
    videoAwal: VideoTutorialEntity?,
    onDismiss: () -> Unit,
    onSave: (VideoTutorialEntity) -> Unit
) {
    var judul by remember { mutableStateOf(videoAwal?.judul ?: "") }
    var kategori by remember { mutableStateOf(videoAwal?.kategori ?: "") }
    var deskripsi by remember { mutableStateOf(videoAwal?.deskripsi ?: "") }
    var videoUrl by remember { mutableStateOf(videoAwal?.videoUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (videoAwal == null) "Tambah Video" else "Edit Video",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Judul video") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = kategori,
                    onValueChange = { kategori = it },
                    label = { Text("Kategori") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { videoUrl = it },
                    label = { Text("URL video (YouTube)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = videoUrl.isNotBlank() && !YoutubeUtils.isYoutubeUrl(videoUrl),
                    supportingText = {
                        if (videoUrl.isNotBlank() && !YoutubeUtils.isYoutubeUrl(videoUrl)) {
                            Text(text = "Bukan URL YouTube yang valid")
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (judul.isNotBlank() && kategori.isNotBlank() && YoutubeUtils.isYoutubeUrl(videoUrl)) {
                        onSave(
                            VideoTutorialEntity(
                                idVideo = videoAwal?.idVideo ?: 0,
                                judul = judul,
                                kategori = kategori,
                                deskripsi = deskripsi,
                                videoUrl = videoUrl
                            )
                        )
                    }
                }
            ) {
                Text(text = "Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Batal")
            }
        }
    )
}
