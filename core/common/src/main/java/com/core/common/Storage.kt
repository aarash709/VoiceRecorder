package com.core.common

import android.content.Context
import com.core.common.model.Voice
import java.io.File

class Storage {

    fun getPath(context: Context): String {
        return context.getExternalFilesDir(null)?.path.toString()
    }

    fun getVoices(context: Context): List<Voice>? {
        val path = getPath(context)
        return File(
            path,
        ).listFiles()?.map { voiceFile ->
            val recordTime = getLastTimeRecorded(voiceFile.lastModified())
            val durationInMillis = mediaDurationInMillis(voiceFile.path)
            val seconds = getSeconds(durationInMillis).doubleDigitFormat()
            val minutes = getMinutes(durationInMillis).doubleDigitFormat()
            Voice(
                title = voiceFile.name,
                path = voiceFile.path,
                duration = "$minutes:$seconds",
                recordTime = recordTime,
            )
        }
    }

}