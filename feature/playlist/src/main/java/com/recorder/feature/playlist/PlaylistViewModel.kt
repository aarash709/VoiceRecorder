package com.recorder.feature.playlist

import android.media.MediaPlayer
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_RECORDINGS
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.common.model.Voice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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

    private val _voices = MutableStateFlow(emptyList<Voice>())
    val voices = _voices.asStateFlow()

    init {
        getVoices()
    }

    fun onPlay(nextVoiceIndex: Int, newVoice: Voice) {
        viewModelScope.launch {
            Timber.e(newVoice.path)
            currentPlayingVoice = newVoice
            currentVoiceIndex = nextVoiceIndex
            currentPlayingVoice?.let { currentVoice ->
                if (!_isPlaying.value) {
                    startPlayback(currentVoice)
                } else {
                    stopPlayback()
                    startPlayback(newVoice)
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

    fun onStop() {
        stopPlayback()
    }

    fun onPlayPause() {
        if (_isPlaying.value) {
            pausePlayback()
        } else {
            resumePlayback()
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

    private fun pausePlayback() {
        mediaPlayer?.pause()
//        updateAppState(AppSate.OnIdle)
        Timber.e("Paused")
        _isPlaying.value = false
    }

    private fun resumePlayback() {
        mediaPlayer.apply {
            start()
            Timber.e("Resumed")
        }
    }

    private fun getVoices() {
        val DIRECTORY_NAME = "VoiceRecorder"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val voicePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_RECORDINGS).path
            File(
                voicePath,
                DIRECTORY_NAME
            ).listFiles()?.map {
                Voice(
                    title = it.name,
                    path = it.path,
                )
            }?.let { voices ->
                _voices.value = voices
                Timber.e("recordings:$voices")
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//            val voicePath = Environment.getExternalStorageDirectory().path
            _voices.value = emptyList()
        }
    }

}

