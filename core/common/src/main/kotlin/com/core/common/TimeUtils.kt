package com.core.common

import android.media.MediaMetadataRetriever
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Calculates the time a file is saved on storage.
 * [lastModified] get the time of file creation by using File().lastModified.
 * */
fun getLastTimeRecorded(lastModified: Long): String {
    val currentTime = Date().time
    val timeToCalculate = currentTime - lastModified
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeToCalculate)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeToCalculate)
    val hours = TimeUnit.MILLISECONDS.toHours(timeToCalculate)
    val days = TimeUnit.MILLISECONDS.toDays(timeToCalculate)

    return when {
        seconds in 0..60 -> "Just now"
        minutes in 1..59 -> "$minutes minutes ago"
        hours == 1L -> "An hour ago"
        hours in 2..24 -> "$hours hours ago"
        days == 1L -> "Yesterday"
        else -> "$days days ago"

    }
}

/**
 * Returns the duration of a media file using MediaMetadataRetriever()
 * [path] the path of a file
 * */
fun mediaDurationInMillis(path: String): Long {
    val time = MediaMetadataRetriever().run {
        setDataSource(path)
        extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    }
    return time?.toLong() ?: 0L
}

fun Long.doubleDigitFormat(pattern: String = "%02d"): String {
    return String.format(pattern, this)
}

fun getSeconds(millis: Long): Long {
    return TimeUnit.MILLISECONDS.toSeconds(millis)
}
fun getMinutes(millis: Long): Long {
    return TimeUnit.MILLISECONDS.toMinutes(millis)
}