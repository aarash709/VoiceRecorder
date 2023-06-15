package com.recorder.feature.playlist

import android.media.MediaPlayer
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_RECORDINGS
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.core.common.model.Voice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.File
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor() : ViewModel() {


    private lateinit var mediaPlayer: MediaPlayer
    private var previousVoiceIndex: Int? = null
    private var voiceToPlay: Voice? = null
    val isPlaying = mutableStateOf(false)

    val _voices = MutableStateFlow(List(5) { Voice("title$it") })
    val voices = _voices.asStateFlow()

    init {
        getVoices().map {
            _voices.value = it
        }
    }

    fun onPlay(nextVoiceIndex: Int, voice: Voice) {
        voiceToPlay = voice
        voiceToPlay?.let {
            if (!isPlaying.value) {
                previousVoiceIndex = nextVoiceIndex
                startPlayback(it, previousVoiceIndex!!)
            } else {
                stopPlayback(previousVoiceIndex!!)
                startPlayback(it, nextVoiceIndex)
                previousVoiceIndex = nextVoiceIndex
            }
        }
    }

    fun onPlayPause() {
        if (isPlaying.value) {
            pausePlayback()
        } else {
            resumePlayback()
        }
    }

    private fun startPlayback(voice: Voice, index: Int) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            if (isPlaying) stopPlayback(index)
            try {
                setDataSource(voice.path)
                prepare()
                start()
//                updateAppState(AppSate.Playing)
//                voiceDuration.value = duration
                Timber.e(voice.title)
                Timber.e("playback started")
//                this@MainViewModel.isPlaying.value = isPlaying
            } catch (e: Exception) {
//                this@MainViewModel.isPlaying.value = isPlaying
                Timber.e("playback failed")
            }
        }
//        onPlayUpdateListState(index)
//        playbackAllowed.value = false
        Timber.e("started playback: " + isPlaying.value)
        mediaPlayer?.setOnCompletionListener {
            stopPlayback(index)
        }
    }

    fun stopPlayback(index: Int) {
        mediaPlayer?.apply {
            stop()
//            updateAppState(AppSate.OnIdle)
            Timber.e("playback stopped")
//            this@MainViewModel.isPlaying.value = isPlaying
        }
//        onPlayUpdateListState(index)
//        playbackAllowed.value = true
        Timber.e("started playback(on stop): " + isPlaying.value)
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
//        updateAppState(AppSate.OnIdle)
        Timber.e("Paused")
        isPlaying.value = false
    }

    private fun resumePlayback() {
        mediaPlayer?.apply {
            start()
            Timber.e("Resumed")
//            this@MainViewModel.isPlaying.value = true
        }
//        updateAppState(AppSate.Playing)
    }

    private fun getVoices(): Flow<List<Voice>> = flow {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val voicePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_RECORDINGS).path
            val voiceList = File(voicePath).listFiles()?.map {
                Voice(
                    title = it.name,
                    path = it.path,
                )
            }?.let {
                emit(it)
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//            val voicePath = Environment.getExternalStorageDirectory().path
            emit(emptyList())
        }
    }

}