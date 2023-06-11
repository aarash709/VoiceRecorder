package com.recorder.feature.record

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_RECORDINGS
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

const val DIRECTORY_NAME = "VoiceRecorder"

@HiltViewModel
class RecordViewModel @Inject constructor() : ViewModel() {

    private lateinit var mediaRecorder: MediaRecorder
    private var fileName = mutableStateOf("")
    private var canAccessAppFolder = false
    private val recordingAllowed = mutableStateOf(true)
    private var isRecording = false
    private var directoryName = ""

    private var _recordTime = MutableStateFlow(0)
    val recordTime = _recordTime.asStateFlow()

    init {
        initializeAppSettings()
    }

    fun onRecord(context: Context) {
        if (isRecording) {
            startRecordingAudio(
                context = context,
                onRecord = {
                    isRecording = true
                    startTimer()
                }
            )
        } else {
            stopRecordingAudio(
                onStopRecording = {
                    isRecording = false
                    stopTimer()
                })
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            delay(100L)
            _recordTime.update { it + 1 }
        }
    }
    private fun stopTimer(){
        viewModelScope.launch {
            _recordTime.update { 0 }
        }
    }

    private fun startRecordingAudio(context: Context, onRecord: () -> Unit) {
        val name = generateFileName()
        if (canAccessAppFolder) {
            val file = File(directoryName, name)
            fileName.value = file.path
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(fileName.value)
                try {
                    prepare()
                } catch (e: Exception) {
                    Timber.e("recorder can`t be prepared")
                }
                start()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mediaRecorder = MediaRecorder(context).apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(fileName.value)
                    try {
                        prepare()
                    } catch (e: Exception) {
                        Timber.e("recorder on android(S) can`t be prepared")
                    }
                    start()
                }
            }
            onRecord()
//            updateAppState(AppSate.Recording)
//            appState.value = VoiceRecorderState.STATE_RECORDING
//            recordingAllowed.value = false
//            playbackAllowed.value = false
//            isRecording.value = true
//            Timber.e("is recording: " + isRecording.value)
        } else {
            Timber.e("cannot access app dir")
        }
    }

    private fun stopRecordingAudio(onStopRecording: () -> Unit) {
//        timer.value = DEFAULT_RECORD_TIMER_VALUE
        mediaRecorder.apply {
            stop()
            release()
            onStopRecording()
        }
//        Timber.e("is recording: " + isRecording.value)
    }

    private fun generateFileName(
        pattern: String = "yyMMdd_HHmmss",
        fileExt: String = ".m4a",
        local: Locale = Locale.getDefault(),
    ): String {
        val sdf = SimpleDateFormat(pattern, local)
        val date = sdf.format(Date())
        return "$date$fileExt"
    }

    private fun createStorageFolder() {
        val path = storagePath()
        val folderExists = File(path).exists()
        canAccessAppFolder = when {
            folderExists -> {
                Timber.e("$DIRECTORY_NAME exists")
                true
            }

            File(path, "/$DIRECTORY_NAME").mkdirs() -> {
                Timber.e("file created")
                true
            }

            else -> {
                Timber.e("something went wrong, no folder")
                false
            }
        }
    }

    private fun storagePath(): String {
        val path = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                Environment.getExternalStoragePublicDirectory(DIRECTORY_RECORDINGS).path
            }

            else -> {
                Environment.getExternalStorageDirectory().path
            }
        }
        Timber.e(Environment.getExternalStorageDirectory().path)
        return path
    }

    private fun initializeAppSettings() {
        createStorageFolder()
        val rootPath = storagePath()
        directoryName = "$rootPath/$DIRECTORY_NAME"
    }
}