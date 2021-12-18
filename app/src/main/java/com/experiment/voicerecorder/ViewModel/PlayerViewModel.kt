package com.experiment.voicerecorder.ViewModel

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Environment
import android.os.IBinder
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.experiment.voicerecorder.Utils.FileSavedTime
import com.experiment.voicerecorder.data.Voice
import com.experiment.voicerecorder.service.player.PlayerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File


class PlayerViewModel(val app: Application):AndroidViewModel(app) {

    var allVoices = mutableStateListOf<Voice>()

    fun getAllVoices() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                File(getStoragePath(),
                    "/$DIRECTORY_NAME").listFiles()
                    ?.map {
                        allVoices.add(Voice(
                            it.name,
                            it.absolutePath,
                            false,
                            "",
                            FileSavedTime().getLastTimeRecorded(it.lastModified())
                        ))
                    }
            }
            Timber.e("loading all voices")
        }
    }

    private fun getStoragePath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }
}