package com.aplikasis.fittrack.utils

object YoutubeUtils {

    fun getYoutubeThumbnail(url: String): String {

        return try {

            val videoId = when {

                url.contains("watch?v=") ->
                    url.substringAfter("watch?v=").substringBefore("&")

                url.contains("youtu.be/") ->
                    url.substringAfter("youtu.be/").substringBefore("?")

                else ->
                    ""
            }

            "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

        } catch (e: Exception) {
            ""
        }
    }
}