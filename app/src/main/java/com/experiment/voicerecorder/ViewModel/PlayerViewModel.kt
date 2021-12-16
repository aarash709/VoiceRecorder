package com.experiment.voicerecorder.ViewModel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.experiment.voicerecorder.PlayerActivity
import com.experiment.voicerecorder.Utils.FileSavedTime
import com.experiment.voicerecorder.data.Voice
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


class PlayerViewModel(app: Application):AndroidViewModel(app) {

    var allVoices = mutableStateListOf<Voice>()

    fun getAllVoices() {
        viewModelScope.launch {
            val a = File(getStoragePath(),
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
            Timber.e("loading all voices")
        }
    }
    fun startPlaying(){
        //start service
        //then start playback?
    }
    private fun getStoragePath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }
}