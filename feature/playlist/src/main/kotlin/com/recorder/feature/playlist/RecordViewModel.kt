package com.recorder.feature.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor() : ViewModel() {

    private var _timerMillis = MutableStateFlow(0L)

    private val timePattern = DateTimeFormatter.ofPattern("mm:ss")

    val formattedTimer = _timerMillis.map { elapsedTime ->
        LocalTime.ofNanoOfDay(elapsedTime * 1_000_000).format(timePattern)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000L),
        initialValue = "00:00:00"
    )

    private fun startTimer(isRecording: Boolean, currentTime: Long? = null): Flow<Long> = flow {
        var startMillis = currentTime ?: System.currentTimeMillis()
        Timber.e(isRecording.toString())
        while (isRecording) {
            val currentMillis = System.currentTimeMillis()
            val elapsedTimeSinceStart =
                if (currentMillis > startMillis)
                    currentMillis - startMillis
                else
                    0L
            emit(elapsedTimeSinceStart)
            startMillis = System.currentTimeMillis()
            delay(100L)
        }
    }

    private fun resetTimer() {
        viewModelScope.launch {
            _timerMillis.update { 0 }
        }
    }

}