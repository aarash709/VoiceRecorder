package com.recorder.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.core.common.Storage
import com.core.common.model.Voice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService() : Service() {

    @Inject
    private lateinit var storage: Storage
    private lateinit var player: MediaPlayer

    private val binder = LocalBinder()
    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    private val _isPlaying = MutableStateFlow(false)
    private val _voices = MutableStateFlow(listOf<Voice>())

    inner class LocalBinder() : Binder() {
        fun getBinder() = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun play(voice: Voice) {
        serviceScope.launch {
            player.apply {
                try {
                    setDataSource(voice.path)
                    prepare()
                    start()

                    Timber.e(voice.title)
                    Timber.e("playback started")
                } catch (e: Exception) {
                    Timber.e("playback failed")
                }
                _isPlaying.update { isPlaying }
            }
            player.setOnCompletionListener {
                stop()
                Timber.e("on completion")
            }
//            updateVoiceList()
        }
    }

    fun stop() {
        serviceScope.launch {
            player.apply {
                stop()
                _isPlaying.update { isPlaying }
            }
            if (!_isPlaying.value)
                Timber.e("playback stopped")
//            updateVoiceList()
            Timber.e("is playing(on stop): " + _isPlaying.value)
        }
    }

    fun pause() {
        serviceScope.launch {
            player.apply {
                pause()
            }
        }
    }

    fun resume() {
        serviceScope.launch {
            player.apply {
                resume()
            }
        }
    }

    fun getVoices(context: Context) {
        _voices.update { storage.getVoices(context) ?: listOf() }
    }

}