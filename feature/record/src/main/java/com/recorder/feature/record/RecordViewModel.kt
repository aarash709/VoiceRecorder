package com.recorder.feature.record

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_RECORDINGS
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

const val DIRECTORY_NAME = "VoiceRecorder"

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class RecordViewModel @Inject constructor() : ViewModel() {

    private lateinit var mediaRecorder: MediaRecorder
    private var fileName = mutableStateOf("")
    private var canAccessAppFolder = false
    private val recordingAllowed = mutableStateOf(true)
    val isRecording = mutableStateOf(false)
    private var directoryName = ""

    init {
        initializeAppSettings()
//      notification.createNotificationChannel(app)
    }

    fun onRecord(context: Context) {
//        val intent = Intent(context, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(app, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        if (recordingAllowed.value) {
            startRecordingAudio(
                context = context,
                onRecord = {
//                notification.showNotification(
//                    app,
//                    RECORDING_CHANNEL_ID,
//                    R.drawable.ic_record,
//                    "Voice Recorder",
//                    "Now Recording",
//                    pendingIntent
//                )
                }
            )
        } else {
            stopRecordingAudio(
                onStopRecording = {
//                notification.removeNotification(app, RECORDING_ID)
                })
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
//        mediaRecorder = null
//        updateAppState(AppSate.OnIdle)
//        recordingAllowed.value = true
//        isRecording.value = false
//        playbackAllowed.value = true
        Timber.e("is recording: " + isRecording.value)
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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun createStorageFolder() {
        val path = storagePath()
        // TODO: 11/29/2021 should handle exceptions later on
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

    @RequiresApi(Build.VERSION_CODES.S)
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
//    Environment.getExternalStorageDirectory().path
        //inside app internal
//        val contextPath = app.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        //storage root path
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initializeAppSettings() {
        createStorageFolder()
        val rootPath = storagePath()
        directoryName = "$rootPath/$DIRECTORY_NAME"
//        updateAppState(AppSate.OnIdle)
    }
}