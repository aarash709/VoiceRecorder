package com.core.common

import java.util.Date
import java.util.concurrent.TimeUnit
class FileSavedTime {
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
}