package com.core.common

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import com.core.common.model.Voice
import java.io.File
import java.util.concurrent.TimeUnit

class Storage {

    fun getPath(context: Context): String {
        return context.getExternalFilesDir(null)?.path.toString()
    }

    fun getVoices(context: Context): List<Voice>? {
        val path = getPath(context)
        return File(
            path,
        ).listFiles()?.map { voiceFile ->
            val savedTime = FileSavedTime().getLastTimeRecorded(voiceFile.lastModified())
            val durationMillis = MediaMetadataRetriever().run {
                setDataSource(voiceFile.path)
                extractMetadata(METADATA_KEY_DURATION)
            }.toString().toLong()
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
            Voice(
                title = voiceFile.name,
                path = voiceFile.path,
                duration = "${String.format("%02d", minutes)}:${String.format("%02d",seconds)}",
                recordTime = savedTime,
            )
        }
    }

}