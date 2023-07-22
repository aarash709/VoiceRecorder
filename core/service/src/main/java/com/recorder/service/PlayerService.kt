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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService() : Service() {

    @Inject
    lateinit var storage: Storage
    private lateinit var player: MediaPlayer

    private val binder = LocalBinder()
    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    private var _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _voices = MutableStateFlow(listOf<Voice>())

    inner class LocalBinder : Binder() {
        fun getService() = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()
        Timber.e("player service created")
        player = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder {
        Timber.e("${this.javaClass.simpleName} binded")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.e("${this.javaClass.simpleName} unbinded")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "play" -> {
                val voice =
                    Json.decodeFromString<Voice>(intent.extras?.getString("voice").toString())
                Timber.e(intent.extras?.getString("voice").toString())
                play(voice)
            }

            "stop" -> {
                stop()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("player service destroyed")
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
                    Timber.e(e.message)
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
                reset()
                _isPlaying.update { isPlaying }
            }
            if (!isPlaying.value)
                Timber.e("playback stopped")
//            updateVoiceList()
            Timber.e("is playing(on stop): " + isPlaying.value)
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