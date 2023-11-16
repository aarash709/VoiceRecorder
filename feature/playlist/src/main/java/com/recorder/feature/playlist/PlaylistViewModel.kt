package com.recorder.feature.playlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.common.Storage
import com.core.common.model.Voice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    fun updateVoiceList(selectedVoiceIndex: Int, isPlaying: Boolean = false) {
        viewModelScope.launch {
            _voices.update { voices ->
                voices.mapIndexed { index, voice ->
                    when {
                        index == selectedVoiceIndex && isPlaying -> {
                            voice.copy(isPlaying = isPlaying)
                        }

                        index == selectedVoiceIndex && !isPlaying -> {
                            voice.copy(isPlaying = isPlaying)
                        }

                        else -> voice.copy(isPlaying = false)
                    }
                }
            }
        }
    }
    fun deleteVoice(voice: Voice){

    }

    fun renameVoice(voice: Voice){

    }
}

