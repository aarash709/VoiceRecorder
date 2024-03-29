package com.recorder.feature.record

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recorder.service.RecorderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
class RecordViewModel @Inject constructor() : ViewModel() {

    private var _isRecording = MutableStateFlow(false)
    var isRecording = _isRecording.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000L),
        initialValue = false
    )
    private var _timerMillis = MutableStateFlow(0L)

    private val timePattern = DateTimeFormatter.ofPattern("mm:ss")

    val formattedTimer = _timerMillis.map { elapsedTime ->
        LocalTime.ofNanoOfDay(elapsedTime * 1_000_000).format(timePattern)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1_000L),
        initialValue = "00:00:00"
    )

    init {
        _isRecording.flatMapLatest {
            startTimer(it)
        }.onEach { time ->
            _timerMillis.update { it + time }
        }.launchIn(viewModelScope)
    }

    fun onRecord(context: Context) {
        if (_isRecording.value.not()) {
            Intent(context, RecorderService::class.java).also {
                it.action = "record"
                context.startService(it)
                _isRecording.update { true }
            }
        } else {
            Intent(context, RecorderService::class.java).also {
                it.action = "stop"
                context.startService(it)
                _isRecording.update { false }
                resetTimer()
            }
        }
    }

    fun onPause(context: Context) {
        if (_isRecording.value) {
            Intent(context, RecorderService::class.java).also {
                it.action = "pause"
                context.startService(it)
            }
            _isRecording.update { false }
            _timerMillis.update { it }
        } else {
            Intent(context, RecorderService::class.java).also {
                it.action = "resume"
                context.startService(it)
                _isRecording.update { true }
                startTimer(_isRecording.value, _timerMillis.value)
            }
        }
    }

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