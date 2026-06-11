package com.aplikasis.fittrack.utils

import java.net.URLEncoder

object YoutubeUtils {
    private const val EMBED_ORIGIN = "https://fittrack.local"

    fun extractVideoId(url: String): String? {
        val cleanUrl = url.trim()
        val lowerUrl = cleanUrl.lowercase()

        return try {
            when {
                lowerUrl.contains("youtu.be/") -> {
                    cleanUrl.substringAfterMarker("youtu.be/")
                        .substringBefore("?")
                        .substringBefore("&")
                        .substringBefore("/")
                        .trim()
                }

                lowerUrl.contains("youtube.com/watch") -> {
                    cleanUrl.substringAfter("?")
                        .split("&")
                        .firstOrNull { it.startsWith("v=", ignoreCase = true) }
                        ?.substringAfter("v=")
                        ?.substringBefore("&")
                        ?.trim()
                }

                lowerUrl.contains("youtube.com/shorts/") -> {
                    cleanUrl.substringAfterMarker("youtube.com/shorts/")
                        .substringBefore("?")
                        .substringBefore("/")
                        .trim()
                }

                lowerUrl.contains("youtube.com/live/") -> {
                    cleanUrl.substringAfterMarker("youtube.com/live/")
                        .substringBefore("?")
                        .substringBefore("/")
                        .trim()
                }

                lowerUrl.contains("youtube.com/embed/") -> {
                    cleanUrl.substringAfterMarker("youtube.com/embed/")
                        .substringBefore("?")
                        .substringBefore("/")
                        .trim()
                }

                lowerUrl.contains("youtube-nocookie.com/embed/") -> {
                    cleanUrl.substringAfterMarker("youtube-nocookie.com/embed/")
                        .substringBefore("?")
                        .substringBefore("/")
                        .trim()
                }

                else -> null
            }?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    fun toEmbedUrl(url: String): String {
        val videoId = extractVideoId(url) ?: return url.trim()
        val origin = URLEncoder.encode(EMBED_ORIGIN, "UTF-8")
        return "https://www.youtube-nocookie.com/embed/$videoId" +
            "?autoplay=1&playsinline=1&fs=1&rel=0&modestbranding=1&enablejsapi=1&origin=$origin"
    }

    fun isYoutubeUrl(url: String): Boolean {
        val cleanUrl = url.trim().lowercase()
        return cleanUrl.contains("youtube.com") ||
            cleanUrl.contains("youtube-nocookie.com") ||
            cleanUrl.contains("youtu.be")
    }

    fun getYoutubeThumbnail(url: String): String {
        val videoId = extractVideoId(url)
        return if (videoId != null) {
            "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        } else {
            url
        }
    }

    private fun String.substringAfterMarker(marker: String): String {
        val index = indexOf(marker, ignoreCase = true)
        return if (index >= 0) substring(index + marker.length) else this
    }
}
