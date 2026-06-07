package com.aplikasis.fittrack.ui.screen

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aplikasis.fittrack.data.entity.VideoTutorialEntity
import com.aplikasis.fittrack.utils.YoutubeUtils

/**
 * FITUR 1 - Perubahan VideoDetailScreen:
 * - Hapus Intent.ACTION_VIEW / Uri.parse (video tidak lagi dibuka di luar aplikasi)
 * - Ganti tombol "Tonton di Youtube" dengan embedded YouTube player menggunakan WebView
 * - Player mendukung: Play, Pause, Seek, Fullscreen (via WebChromeClient)
 * - URL non-YouTube (MP4 direct link) juga didukung via WebView dengan tag <video>
 *
 * Catatan AndroidManifest yang diperlukan (lihat bagian integrasi di output):
 *   <uses-permission android:name="android.permission.INTERNET" />
 *   di MainActivity: android:hardwareAccelerated="true"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    videoId: Long,
    viewModel: VideoDetailViewModel,
    onBackClick: () -> Unit
) {
    var video by remember { mutableStateOf<VideoTutorialEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(videoId) {
        video = viewModel.getVideo(videoId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Video") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            video != null -> {
                val currentVideo = video!!
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // ── Embedded Player ───────────────────────────────────────
                    EmbeddedVideoPlayer(
                        url = currentVideo.videoUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color.Black)
                    )

                    // ── Info Video ────────────────────────────────────────────
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentVideo.judul,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(Modifier.height(10.dp))

                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = currentVideo.kategori,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = currentVideo.deskripsi,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF374151)
                        )
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Video tidak ditemukan")
                }
            }
        }
    }
}

/**
 * Komponen embedded video player menggunakan WebView.
 *
 * - Jika URL adalah YouTube → gunakan YouTube IFrame API (embed URL)
 * - Jika URL adalah MP4/direct link → gunakan HTML5 <video> tag
 *
 * Mendukung fullscreen via WebChromeClient.onShowCustomView.
 */
@Composable
fun EmbeddedVideoPlayer(
    url: String,
    modifier: Modifier = Modifier
) {
    val embedUrl = remember(url) { YoutubeUtils.toEmbedUrl(url) }
    val isYoutube = remember(url) { YoutubeUtils.isYoutubeUrl(url) }

    val htmlContent = remember(embedUrl, isYoutube) {
        if (isYoutube) {
            // YouTube IFrame embed — fullscreen diizinkan lewat allow="fullscreen"
            """
            <!DOCTYPE html>
            <html>
            <head>
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <style>
                * { margin:0; padding:0; box-sizing:border-box; }
                body { background:#000; width:100%; height:100%; }
                .video-container { position:relative; width:100%; height:0; padding-bottom:56.25%; }
                iframe { position:absolute; top:0; left:0; width:100%; height:100%; border:none; }
              </style>
            </head>
            <body>
              <div class="video-container">
                <iframe
                  src="$embedUrl"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; fullscreen"
                  allowfullscreen>
                </iframe>
              </div>
            </body>
            </html>
            """.trimIndent()
        } else {
            // Direct MP4 / generic video URL
            """
            <!DOCTYPE html>
            <html>
            <head>
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <style>
                * { margin:0; padding:0; box-sizing:border-box; }
                body { background:#000; display:flex; align-items:center; justify-content:center; height:100vh; }
                video { width:100%; max-height:100vh; }
              </style>
            </head>
            <body>
              <video controls playsinline>
                <source src="$url" type="video/mp4">
                Browser tidak mendukung tag video.
              </video>
            </body>
            </html>
            """.trimIndent()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                // Agar link di dalam iframe tidak membuka browser eksternal
                webViewClient = WebViewClient()

                // WebChromeClient dibutuhkan untuk fullscreen YouTube
                webChromeClient = WebChromeClient()

                loadDataWithBaseURL(
                    "https://www.youtube.com",
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { webView ->
            // Hanya reload jika konten berubah (misal videoId beda)
            webView.loadDataWithBaseURL(
                "https://www.youtube.com",
                htmlContent,
                "text/html",
                "UTF-8",
                null
            )
        }
    )
}