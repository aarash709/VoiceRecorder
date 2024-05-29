package com.recorder.feature.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RecordViewModel @Inject constructor() : ViewModel() {

    private var _isRecording = MutableStateFlow(false)
    private var isRecording = _isRecording.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000L),
        initialValue = false
    )

    private var _timeRecordingStarted = MutableStateFlow(0L)
    private val timeRecordingStarted = _timeRecordingStarted.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000L),
        initialValue = 0L
    )

    private var _currentRecordingSeconds = MutableStateFlow(0L)
    private val currentRecordingSeconds = _currentRecordingSeconds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1_000L),
            initialValue = 0L
        )

    private val formatter = DateTimeFormatter.ofPattern("mm:ss")
    val formattedTimer = currentRecordingSeconds.map { seconds ->
        Timber.e("timeS:$seconds")
        val safeSeconds = if (seconds in 0..86399) seconds else 0
        formatter.format(LocalTime.ofSecondOfDay(safeSeconds))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000L),
        initialValue = "00:00"
    )

    init {
        isRecording.combine(timeRecordingStarted) { isRecording, seconds ->
            if (!isRecording) resetTimer()
            setTimer(isRecording, seconds)
        }.flatMapLatest {
            it
        }.onEach { currentSecond ->
            _currentRecordingSeconds.update { it + currentSecond }
        }.launchIn(viewModelScope)
    }

    fun updateRecordState(isRecording: Boolean, currentTime: Long? = null) {
        viewModelScope.launch {
            _isRecording.update { isRecording }
            _timeRecordingStarted.update { currentTime ?: 0L }
        }
    }

    private fun setTimer(isRecording: Boolean, currentTime: Long? = null) = flow {
        var startMillis = currentTime ?: System.currentTimeMillis().milliseconds.inWholeSeconds
        while (isRecording) {
            val currentMillis = System.currentTimeMillis().milliseconds.inWholeSeconds
            val elapsedTimeSinceStart =
                if (currentMillis > startMillis)
                    currentMillis - startMillis
                else
                    0L
            emit(elapsedTimeSinceStart)
            startMillis = System.currentTimeMillis().milliseconds.inWholeSeconds
            delay(1000L)
        }
    }

    private fun resetTimer() {
        viewModelScope.launch {
            _currentRecordingSeconds.update { 0 }
        }
    }

}