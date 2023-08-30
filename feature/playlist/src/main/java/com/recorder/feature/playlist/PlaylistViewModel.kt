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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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
class PlaylistViewModel @Inject constructor(
    private val storage: Storage,
) : ViewModel() {


    private val _voices = MutableStateFlow(listOf<Voice>())
    val voices = _voices.stateIn(
        scope= viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = listOf()
    )

    fun getVoices(context: Context) {
        _voices.update { storage.getVoices(context) ?: listOf() }
    }

}

