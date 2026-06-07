package com.aplikasis.fittrack.utils

/**
 * Utility untuk menangani URL YouTube.
 *
 * FITUR 1 - Tambahan:
 * - [toEmbedUrl]: konversi URL YouTube biasa ke format embed
 * - [isYoutubeUrl]: deteksi apakah URL adalah YouTube
 *
 * Format URL YouTube yang didukung:
 *   https://www.youtube.com/watch?v=VIDEO_ID
 *   https://youtu.be/VIDEO_ID
 *   https://www.youtube.com/shorts/VIDEO_ID
 *   https://www.youtube.com/embed/VIDEO_ID (sudah embed, langsung pakai)
 */
object YoutubeUtils {

    /**
     * Ekstrak video ID dari berbagai format URL YouTube.
     * Return null jika bukan URL YouTube atau format tidak dikenali.
     */
    fun extractVideoId(url: String): String? {
        return try {
            when {
                // Format: youtu.be/VIDEO_ID
                url.contains("youtu.be/") -> {
                    url.substringAfter("youtu.be/")
                        .substringBefore("?")
                        .substringBefore("&")
                        .trim()
                }

                // Format: youtube.com/watch?v=VIDEO_ID
                url.contains("youtube.com/watch") -> {
                    val queryString = url.substringAfter("?")
                    queryString.split("&")
                        .firstOrNull { it.startsWith("v=") }
                        ?.substringAfter("v=")
                        ?.substringBefore("&")
                        ?.trim()
                }

                // Format: youtube.com/shorts/VIDEO_ID
                url.contains("youtube.com/shorts/") -> {
                    url.substringAfter("youtube.com/shorts/")
                        .substringBefore("?")
                        .substringBefore("/")
                        .trim()
                }

                // Sudah embed: youtube.com/embed/VIDEO_ID
                url.contains("youtube.com/embed/") -> {
                    url.substringAfter("youtube.com/embed/")
                        .substringBefore("?")
                        .substringBefore("/")
                        .trim()
                }

                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Konversi URL YouTube apa pun ke format embed URL.
     * Jika sudah embed atau bukan YouTube → kembalikan URL asli.
     */
    fun toEmbedUrl(url: String): String {
        val videoId = extractVideoId(url) ?: return url
        // ?rel=0 → nonaktifkan suggested videos; ?modestbranding=1 → kurangi branding
        return "https://www.youtube.com/embed/$videoId?rel=0&modestbranding=1"
    }

    /**
     * Cek apakah URL adalah YouTube (termasuk youtu.be dan berbagai format).
     */
    fun isYoutubeUrl(url: String): Boolean {
        return url.contains("youtube.com") || url.contains("youtu.be")
    }

    /**
     * Existing function — dipertahankan untuk kompatibilitas dengan VideoTutorialScreen.
     * Mengambil thumbnail YouTube dari video URL.
     */
    fun getYoutubeThumbnail(url: String): String {
        val videoId = extractVideoId(url)
        return if (videoId != null) {
            "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        } else {
            url // Fallback ke URL asli jika bukan YouTube
        }
    }
}