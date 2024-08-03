package com.recorder.feature.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recorder.core.datastore.LocalUserSettings
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RecordViewModel @Inject constructor(
    userSettingsData: LocalUserSettings,
) : ViewModel() {

    val qualitySetting = userSettingsData.getSettings().map {
        it.recordingQuality.name + " quality"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1000L),
        initialValue = ""
    )
    private var _isRecording = MutableStateFlow(false)
    private var isRecording = _isRecording.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000L),
        initialValue = false
    )

    private var _timeRecordingStarted = MutableStateFlow<Long?>(null)
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

    private val formatter = DateTimeFormatter.ofPattern("mm:ss.S")
    val formattedTimer = currentRecordingSeconds.map { millis ->
        val safeNanos =
            if (millis in 0..86399999999999) millis * 1_000_000 else 0 //86399999999999 is 24HRS
        LocalTime.ofNanoOfDay(safeNanos).format(formatter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000L),
        initialValue = "00:00.0"
    )

    init {
        isRecording.combine(timeRecordingStarted) { isRecording, millis ->
            if (!isRecording) resetTimer()
            setTimer(isRecording, millis)
        }.flatMapLatest {
            it
        }.onEach { currentSecond ->
            _currentRecordingSeconds.update { it + currentSecond }
        }.launchIn(viewModelScope)
    }

    fun updateRecordState(isRecording: Boolean, currentTime: Long? = null) {
        viewModelScope.launch {
            _isRecording.update { isRecording }
            _timeRecordingStarted.update { currentTime }
        }
    }

    private fun setTimer(isRecording: Boolean, currentTime: Long? = null) = flow {
        Timber.e("timecur:$currentTime")
        var startMillis = currentTime ?: System.currentTimeMillis()
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
            _currentRecordingSeconds.update { 0 }
        }
    }
}