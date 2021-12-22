package com.experiment.voicerecorder.ViewModel

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Environment
import android.os.IBinder
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.experiment.voicerecorder.Utils.FileSavedTime
import com.experiment.voicerecorder.Utils.VOICE
import com.experiment.voicerecorder.data.Voice
import com.experiment.voicerecorder.service.player.PlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File


class PlayerViewModel(val app: Application) : AndroidViewModel(app) {

    var allVoices = mutableStateOf(listOf(Voice()))
    val isPlaying = mutableStateOf(false)

    fun getAllVoices() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = File(getStoragePath(),
                    "/$DIRECTORY_NAME").listFiles()
                    ?.map {
                        Voice(
                            it.name,
                            it.absolutePath,
                            false,
                            "",
                            FileSavedTime().getLastTimeRecorded(it.lastModified())
                        )
                    }
                items?.let {
                    allVoices.value = it
                }
            }
            Timber.e("loading all voices")
        }
    }

    fun onPlayUpdateListState(index: Int) {
        Timber.e("update list index: $index")
        allVoices.value = allVoices.value.mapIndexed { i, v ->
            if (i == index) {
                if (isPlaying.value)
                    v.copy(isPlaying = isPlaying.value)
                else
                    v.copy(isPlaying = isPlaying.value)
            } else v
        }
    }

    private fun getStoragePath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }
}