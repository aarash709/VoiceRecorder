package com.core.common

import android.content.Context
import android.os.Build
import android.os.Environment
import com.core.common.model.Voice
import java.io.File

class Storage() {

    fun getPath(context: Context): String {
        return context.getExternalFilesDir(null)?.path.toString()
    }

    fun getVoices(context: Context): List<Voice>? {
        val path = getPath(context)
        return File(
        path,
        ).listFiles()?.map { voiceFile ->
            Voice(
                title = voiceFile.name,
                path = voiceFile.path,
            )
        }
    }

}