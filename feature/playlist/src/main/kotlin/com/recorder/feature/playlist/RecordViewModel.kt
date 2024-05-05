package com.recorder.feature.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class RecordViewModel @Inject constructor() : ViewModel() {

    private var _timerMillis = MutableStateFlow(0L)

    private val timePattern = DateTimeFormatter.ofPattern("mm:ss")

    val formattedTimer = _timerMillis.map { elapsedTime ->
        timePattern.format(LocalTime.ofSecondOfDay(elapsedTime))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000L),
        initialValue = "00:00:00"
    )

    fun startTimer(isRecording: Boolean, currentTime: Long? = null) {
        viewModelScope.launch {
            var startMillis = currentTime ?: System.currentTimeMillis().milliseconds.inWholeSeconds
            while (isRecording) {
                val currentMillis = System.currentTimeMillis().milliseconds.inWholeSeconds
                val elapsedTimeSinceStart =
                    if (currentMillis > startMillis)
                        currentMillis - startMillis
                    else
                        0L
                _timerMillis.update { it + elapsedTimeSinceStart }
                startMillis = System.currentTimeMillis().milliseconds.inWholeSeconds
                delay(1000L)
            }
        }
    }

    fun resetTimer() {
        viewModelScope.launch {
            _timerMillis.update { 0 }
        }
    }

}