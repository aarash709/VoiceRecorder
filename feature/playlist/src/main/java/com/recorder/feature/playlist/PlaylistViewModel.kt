package com.recorder.feature.playlist

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_RECORDINGS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.common.Storage
import com.core.common.model.Voice
import com.recorder.service.PlayerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import timber.log.Timber
import java.io.File
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor() : ViewModel() {


    private lateinit var mediaPlayer: MediaPlayer
    private var currentVoiceIndex: Int? = null
    private var currentPlayingVoice: Voice? = null
    private val _isPlaying = MutableStateFlow(false)
    private val storage: Storage = Storage()

    private val _voices = MutableStateFlow(emptyList<Voice>())
    val voices = _voices.asStateFlow()

    fun onPlay(context: Context, nextVoiceIndex: Int, newVoice: Voice) {
        viewModelScope.launch {
            Timber.e("path:${newVoice.path}")
            currentPlayingVoice = newVoice
            currentVoiceIndex = nextVoiceIndex
            currentPlayingVoice?.let { currentVoice ->
                val voice = Json.encodeToString(currentVoice)
                if (!_isPlaying.value) {
                    Intent(context, PlayerService::class.java).also {
                        it.action = "play"
                        it.putExtra("voice",voice)
                        context.startService(it)
                        _isPlaying.update { true }
                    }
                } else {
                    Intent(context, PlayerService::class.java).also {
                        it.action = "stop"
                        context.startService(it)
                        _isPlaying.update { false }
                    }
                }
            }
        }
    }
    private fun updateVoiceList() {
        viewModelScope.launch {
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
    private fun startPlayback(voice: Voice) {
        viewModelScope.launch {
            mediaPlayer = MediaPlayer()
            mediaPlayer.apply {
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
            mediaPlayer.setOnCompletionListener {
                stopPlayback()
                Timber.e("on completion")
            }
            updateVoiceList()
            Timber.e("is playing(on start): " + _isPlaying.value)
        }
    }

    private fun stopPlayback() {
        viewModelScope.launch {
            mediaPlayer.apply {
                stop()
                _isPlaying.update { isPlaying }
            }
            if (!_isPlaying.value)
                Timber.e("playback stopped")
            updateVoiceList()
            Timber.e("is playing(on stop): " + _isPlaying.value)
        }
    }

    fun getVoices(context: Context) {
        _voices.update { storage.getVoices(context) ?: listOf() }
    }

}

