package com.experiment.voicerecorder.ViewModel

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.Handler
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.experiment.voicerecorder.MainActivity
import com.experiment.voicerecorder.R
import com.experiment.voicerecorder.Utils.FileSavedTime
import com.experiment.voicerecorder.data.Voice
import com.experiment.voicerecorder.notification.RECORDING_CHANNEL_ID
import com.experiment.voicerecorder.notification.RECORDING_ID
import com.experiment.voicerecorder.notification.VoiceRecorderNotificationManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val TAG = "VoiceRecorder ViewModel"
const val DIRECTORY_NAME = "VoiceRecorder/voices"
const val APP_NAME = "VoiceRecorderAPP"
const val FILE_NAME = "VoiceRecorder"
const val DEFAULT_RECORD_TIMER_VALUE = "00:00"

enum class VoiceRecorderState {
    STATE_IDLE,
    STATE_RECORDING,
    STATE_NOT_RECORDING,
    STATE_PLAYING,
    STATE_NOT_PLAYING
}
sealed class AppSate {
    object OnIdle: AppSate()
    object OnRecord: AppSate()
    object OnPlay: AppSate()
}
@ExperimentalMaterialApi
@ExperimentalPermissionsApi
class MainViewModel(private val app: Application) : AndroidViewModel(app) {


    //ui states
    val appState = mutableStateOf(VoiceRecorderState.STATE_IDLE)

    private var _appState= Channel<AppSate>()
    val state = _appState.receiveAsFlow()

    val isRecording = mutableStateOf(false)
    val isPlaying = mutableStateOf(false)
    var timer = mutableStateOf(DEFAULT_RECORD_TIMER_VALUE)
    val voices = mutableStateOf(listOf(Voice()))
    var voiceDuration = mutableStateOf(0)
    var seekbarCurrentPosition = mutableStateOf(0)
    //end ui states

    private val recordingAllowed = mutableStateOf(true)
    private val playbackAllowed = mutableStateOf(true)
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    val duration = mutableStateOf(0)
    var fileName = mutableStateOf("")
    private var canAccessAppFolder = false
    private var directoryName = ""
    private var sec = 0L
    private var min = 0L
    private var voiceToPlay: Voice? = null
    private var seekbarRunnable: Runnable? = null
    private var seekbarHandler: Handler? = null

    //dependencies
    private val sharedPreferences =
        app.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
    private val notification = VoiceRecorderNotificationManager(app)

    init {
        initializeAppSettings()
        notification.createNotificationChannel(app)
    }
    private fun updateAppState(state: AppSate){
        viewModelScope.launch {
            _appState.send(state)
        }
    }

    private fun initializeAppSettings() {
        createStorageFolder()
        val rootPath = getStoragePath()
        directoryName = "$rootPath/$DIRECTORY_NAME"
        updateAppState(AppSate.OnIdle)
        //appState.value = VoiceRecorderState.STATE_IDLE

    }

    fun getAllVoices() {
        viewModelScope.launch {
           val items = File(getStoragePath(),
                "/$DIRECTORY_NAME").listFiles()
                ?.map {
                    Voice(
                        it.name,
                        it.absolutePath,
                        false,
                        "",
                        FileSavedTime().getLastTimeRecorded(it.lastModified())
                    )
                }
            items?.let {
                voices.value = it
            }
            Timber.e("loading all voices")
        }
    }


    fun onRecord() {
        val intent = Intent(app, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(app, 0, intent, 0)
        if (recordingAllowed.value) {
            startRecordingAudio() {
                notification.showNotification(
                    app,
                    RECORDING_CHANNEL_ID,
                    R.drawable.ic_record,
                    "Voice Recorder",
                    "Now Recording",
                    pendingIntent
                )
            }
        } else {
            stopRecordingAudio() {
                notification.removeNotification(app, RECORDING_ID)
            }
        }
    }


    fun onPlay(i: Int, voice: Voice) {
        voiceToPlay = voice
        voiceToPlay?.let {
            if (!isPlaying.value) {
                startPlayback(it)
            } else {
                stopPlayback()
//                onPlayUpdateListState(i)
//                startPlayback(it)
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

    suspend fun updateSeekbarPosition() {
        withContext(Dispatchers.IO) {
            mediaPlayer?.let {
                seekbarCurrentPosition.value = it.currentPosition
            }
            Timber.e("seek bar current: ${seekbarCurrentPosition.value}")
            delay(500L)
        }
    }

    private fun startPlayback(voice: Voice) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.apply {
            if (isPlaying) stopPlayback()
            try {
                setDataSource(voice.path)
                prepare()
                start()
                updateAppState(AppSate.OnPlay)
//                appState.value = VoiceRecorderState.STATE_PLAYING
                voiceDuration.value = duration
                Timber.e(voice.title)
                Timber.e("playback started")
                this@MainViewModel.isPlaying.value = true
            } catch (e: Exception) {
                this@MainViewModel.isPlaying.value = false
                Timber.e("playback failed")
            }
        }
        playbackAllowed.value = false
        Timber.e("started playback: " + isPlaying.value)
        mediaPlayer?.setOnCompletionListener {
            stopPlayback()
        }
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            stop()
            updateAppState(AppSate.OnIdle)
//            appState.value = VoiceRecorderState.STATE_IDLE
            Timber.e("playback stopped")
        }
        isPlaying.value = false
        playbackAllowed.value = true
        Timber.e("started playback(on stop): " + isPlaying.value)
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        updateAppState(AppSate.OnIdle)
//        appState.value = VoiceRecorderState.STATE_IDLE
        Timber.e("Paused")
        isPlaying.value = false
    }

    private fun resumePlayback() {
        mediaPlayer?.apply {
            start()
            Timber.e("Resumed")
            this@MainViewModel.isPlaying.value = true
        }
        updateAppState(AppSate.OnPlay)
//        appState.value = VoiceRecorderState.STATE_PLAYING
    }

    private fun startRecordingAudio(onRecord: () -> Unit) {
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
                mediaRecorder = MediaRecorder(app).apply {
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
            } else {

            }
            onRecord()
            updateAppState(AppSate.OnRecord)
//            appState.value = VoiceRecorderState.STATE_RECORDING
            recordingAllowed.value = false
            playbackAllowed.value = false
            isRecording.value = true
            Timber.e("is recording: " + isRecording.value)
        } else {
            Timber.e("cannot access app dir")
        }
    }

    private fun stopRecordingAudio(onStopRecording: () -> Unit) {
        timer.value = DEFAULT_RECORD_TIMER_VALUE
        mediaRecorder?.apply {
            stop()
            release()
            onStopRecording()
        }
        mediaRecorder = null
        updateAppState(AppSate.OnIdle)
//        appState.value = VoiceRecorderState.STATE_NOT_RECORDING
//        appState.value = VoiceRecorderState.STATE_IDLE
        recordingAllowed.value = true
        isRecording.value = false
        playbackAllowed.value = true
        Timber.e("is recording: " + isRecording.value)
    }

    fun onPlayUpdateListState(index: Int) {
        Timber.e("update list index: $index")
        voices.value = voices.value.mapIndexed { i, v ->
            if (i == index) {
                if (isPlaying.value)
                    v.copy(isPlaying = isPlaying.value)
                else
                    v.copy(isPlaying = isPlaying.value)
            } else v
        }
    }


    private fun getStoragePath(): String {
        //specific path external
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        //inside app internal
//        val contextPath = app.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        //storage root path
        return Environment.getExternalStorageDirectory().absolutePath
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
        val rootpath = getStoragePath()
        // TODO: 11/29/2021 should handle exeptions later on
        val folderExists = File(rootpath, "/$DIRECTORY_NAME").exists()
        if (folderExists) {
            Timber.e("$DIRECTORY_NAME exists")
            canAccessAppFolder = true
        } else {
            if (File(rootpath, "/$DIRECTORY_NAME").mkdirs()) {
                canAccessAppFolder = true
                Timber.e("file created")
            } else {
                canAccessAppFolder = false
                Timber.e("something went wrong, no folder")
            }
        }
    }

    fun resetRecordingTimer() {
        timer.value = DEFAULT_RECORD_TIMER_VALUE
        sec = 0L
        min = 0L
    }

    fun resetPlayerValues() {
        seekbarCurrentPosition.value = 0
        voiceDuration.value = 0
    }

    suspend fun updateTimerValues() {
        if (appState.value == VoiceRecorderState.STATE_IDLE) {
            sec = 0L
            min = 0L
        }
        val timeIntervals = 1000L
        delay(timeIntervals)
        sec += 1000L
        if (sec >= 60000L) {
            min += 60L * 1000L
            sec = 0L
        }
        val seconds = TimeUnit.MILLISECONDS.toSeconds(sec)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(min)
        val secondsInString = String.format("%02d", seconds)
        val minutesInString = String.format("%02d", minutes)
        val value = "$minutesInString:$secondsInString"
        timer.value = value
    }

    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
        mediaRecorder = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}