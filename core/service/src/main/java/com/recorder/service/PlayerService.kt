package com.recorder.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.net.toUri
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
class PlayerService : MediaLibraryService() {


    private lateinit var mediaLibrarySession: MediaLibrarySession

    @Inject
    lateinit var storage: Storage
    private lateinit var player: MediaPlayer
    private lateinit var exoPlayer: ExoPlayer

    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)
    private var currentVoiceIndex: Int? = null
    private var currentVoice: Voice? = null

    private val callback = MediaLibraryCallback()

    private var _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _voices = MutableStateFlow(listOf<Voice>())
    val voices = _voices.asStateFlow()

    inner class MediaLibraryCallback() : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            Timber.e("onconnect")
            return super.onConnect(session, controller)
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            Timber.e("ongetchildern")
//            return super.onGetLibraryRoot(session, browser, params)
            val mediaItems = voices.value[0].let {
                val metadata =
                    MediaMetadata.Builder()
                        .setTitle("root")
                        .setIsBrowsable(true)
                        .setIsPlayable(true)
                        .build()
            Timber.e("service root path: ${it.path}")
                MediaItem
                    .Builder()
                    .setMediaId(it.title)
                    .setUri("/storage/emulated/0/Android/data/com.experiment.voicerecorder/files/230728_114117.m4a")
                    .setMediaMetadata(metadata)
                    .build()
            }
            return Futures.immediateFuture(LibraryResult.ofItem(mediaItems, params))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            Timber.e("on get item")
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
            Timber.e("ongetchildern")
            val mediaItems = voices.value.map {
//                Timber.e("service childe: ${it.path}")
                val metadata =
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setArtworkUri(it.path.toUri())
                        .setIsBrowsable(false)
                        .setIsPlayable(true)
                        .build()
                MediaItem
                    .Builder()
                    .setMediaId(it.title)
                    .setMediaMetadata(metadata)
                    .setUri(it.path)
                    .build()
            }
            return Futures.immediateFuture(LibraryResult.ofItemList(mediaItems, params))
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.e("player service created")

        exoPlayer = ExoPlayer.Builder(this).build()
        mediaLibrarySession = MediaLibrarySession.Builder(
            this,
            exoPlayer,
            callback,
        )
            .build()
        getVoices(this)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        Timber.e("getsession")
        return mediaLibrarySession
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