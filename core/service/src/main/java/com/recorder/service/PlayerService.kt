package com.recorder.service

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.core.common.Storage
import com.core.common.model.Voice
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService() : MediaLibraryService() {


    private lateinit var mediaLibrarySession: MediaLibrarySession

    @Inject
    lateinit var storage: Storage
    private lateinit var player: MediaPlayer

    private val binder = LocalBinder()
    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)
    private var currentVoiceIndex: Int? = null
    private var currentVoice: Voice? = null

    private var _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _voices = MutableStateFlow(listOf<Voice>())
    val voices = _voices.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService() = this@PlayerService
    }

    inner class MediaLibraryCallback() : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            return super.onConnect(session, controller)
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return super.onGetLibraryRoot(session, browser, params)
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return super.onGetItem(session, browser, mediaId)
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
//            return super.onGetChildren(session, browser, parentId, page, pageSize, params)
            val mediaItems = voices.value.map {
                val metadata =
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .build()
                MediaItem
                    .Builder()
                    .setMediaMetadata(metadata)
                    .build()
            }
            return Futures.immediateFuture(LibraryResult.ofItemList(mediaItems, params))
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.e("player service created")

        val exoPlayer = ExoPlayer.Builder(this).build()
        mediaLibrarySession = MediaLibrarySession.Builder(
            this,
            exoPlayer,
            MediaLibraryCallback()
        )
            .build()

        getVoices(this)
//        player = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        Timber.e("${this.javaClass.simpleName} is bound")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.e("${this.javaClass.simpleName} unbind")
        return super.onUnbind(intent)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("player service destroyed")
    }

    fun onPlay(voice: Voice, index: Int) {
        if (voice != currentVoice && isPlaying.value) {
            stop()
            play(voice, index)
        }
        if (voice == currentVoice && isPlaying.value) {
            stop()
        } else {
            play(voice = voice, nextVoiceIndex = index)
            currentVoice = voice
        }
    }

    private fun play(voice: Voice, nextVoiceIndex: Int) {
        serviceScope.launch {
            currentVoiceIndex = nextVoiceIndex
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
            updateVoiceList()
        }
    }

    private fun stop() {
        serviceScope.launch {
            player.apply {
                stop()
                reset()
                _isPlaying.update { isPlaying }
            }
            if (!isPlaying.value)
                Timber.e("playback stopped")
            updateVoiceList()
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
                start()
            }
        }
    }

    private fun getVoices(context: Context) {
        _voices.update { storage.getVoices(context) ?: listOf() }
    }

    private fun updateVoiceList() {
        serviceScope.launch {
            _voices.update { voices ->
                voices.mapIndexed { index, voice ->
                    when {
                        index == currentVoiceIndex && _isPlaying.value -> {
                            voice.copy(isPlaying = _isPlaying.value)
                        }

                        index == currentVoiceIndex && !_isPlaying.value -> {
                            voice.copy(isPlaying = _isPlaying.value)
                        }

                        else -> voice.copy(isPlaying = false)
                    }

                }
            }
        }
    }
}