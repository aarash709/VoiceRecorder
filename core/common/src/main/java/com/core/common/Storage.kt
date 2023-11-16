package com.core.common

import android.content.Context
import com.core.common.model.Voice
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    /**
     * Handles voice name generation, if there is a duplicate it appends a number to the right.
     */
    fun generateVoiceName(context: Context): String {
        val path = getPath(context)
        val getSavedVoiceNames = File(path).listFiles()?.map { file ->
            file.name
        }
        val generatedName = generateNewFileName()
        val isNameDuplicated = getSavedVoiceNames!!.any { it == generatedName }
        return if (isNameDuplicated)
            getSavedVoiceNames.last().run {
                val lastChar = get(lastIndex.minus(1)) //ignoring the parenthesis if any
                if (lastChar.isDigit())
                    generatedName.plus("(${lastChar.digitToInt().plus(1)})")
                else
                    generatedName.plus("(2)")//for the first duplicate name
            }
        else
            generatedName
    }

    private fun generateNewFileName(
        pattern: String = "hh.mm, d MMM",
        local: Locale = Locale.getDefault(),
    ): String {
        val sdf = SimpleDateFormat(pattern, local)
        return sdf.format(Date())
    }

    fun deleteVoice(voice: Voice, context: Context) {
        // TODO:WIP test before merging
        val path = getPath(context) + voice.path
        File(path).delete()
    }

    fun renameVoice(currentName: String, newName: String, context: Context) {
        // TODO:WIP test before merging
        val path = getPath(context)
        val oldPath = path + currentName
        val newPath = path + newName
        val oldFile = File(oldPath)
        val newFile = File(newPath)
        oldFile.renameTo(newFile)
    }

}