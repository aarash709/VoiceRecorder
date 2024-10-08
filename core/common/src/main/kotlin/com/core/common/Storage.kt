package com.core.common

import android.content.Context
import com.core.common.model.Voice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Storage {

    fun getPath(context: Context): String {
        return context.getExternalFilesDir(null)?.path.toString()
    }

    suspend fun getVoices(context: Context): List<Voice>? {
        return withContext(Dispatchers.IO) {
            val path = getPath(context)
            File(path).listFiles()?.filter { it.exists() }?.map { voiceFile ->
                val recordTime = getLastTimeRecorded(voiceFile.lastModified())
                val durationInMillis = mediaDurationInMillis(voiceFile.path)
                val seconds = getSeconds(durationInMillis).doubleDigitFormat()
                val minutes = getMinutes(durationInMillis).doubleDigitFormat()
                Voice(
                    title = voiceFile.name,
                    path = voiceFile.path,
                    duration = "$minutes:$seconds",
                    recordTime = recordTime,
                    recordTimeMillis = voiceFile.lastModified()
                )
            }
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

    suspend fun deleteVoice(voiceTitle: List<String>, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                voiceTitle.map {
                    File(getPath(context), it).delete()
                }.last()
            } catch (securityException: SecurityException) {
                Timber.e(securityException.message)
                false
            }
        }
    }

    suspend fun renameVoice(currentName: String, newName: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            val path = getPath(context)
            val currentFile = File(path, currentName)
            val newFile = File(path, newName)
            try {
                currentFile.renameTo(newFile)
            } catch (npe: NullPointerException) {
                Timber.e(npe.message)
                false
            } catch (securityException: SecurityException) {
                Timber.e(securityException.message)
                false
            }
        }
    }
}