package com.recorder.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.core.common.Storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

const val RECORDER_SAMPLE_RATE = 44100
const val RECORDER_BIT_RATE = 128.times(1_000)
const val RECORDER_FORMAT = MediaRecorder.OutputFormat.MPEG_4
const val RECORDER_ENCODER = MediaRecorder.AudioEncoder.AMR_WB
const val RECORDER_SOURCE = AudioSource.MIC

@AndroidEntryPoint
class RecorderService : Service() {

    @Inject
    lateinit var recorder: MediaRecorder

    @Inject
    lateinit var storage: Storage

    @Inject
    lateinit var notificationManager: NotificationManager
    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)
    var recordingState = RecordingState.Idle
    var recordingStartTimeMillis = 0L


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()
        NotificationChannel(
            "recorder_channel",
            "Recorder",
            NotificationManager.IMPORTANCE_DEFAULT
        ).let {
            notificationManager.createNotificationChannel(it)
        }
    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getRecorderService() = this@RecorderService
    }

    override fun onBind(intent: Intent?): IBinder {
        Timber.e("binding")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.e("Unbinding")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseResources()
        Timber.e("recorder service destroyed")
    }

    fun startRecording(context: Context) {
        serviceScope.launch {
            val path = storage.getPath(context)
            val voiceName = storage.generateVoiceName(context)
            val file = File(path, voiceName)
            recorder.apply {
                setAudioSource(RECORDER_SOURCE)
                setOutputFormat(RECORDER_FORMAT)
                setAudioSamplingRate(RECORDER_SAMPLE_RATE)
                setAudioEncodingBitRate(RECORDER_BIT_RATE)
                setAudioEncoder(RECORDER_ENCODER)
                setOutputFile(file.path)
                try {
                    prepare()
                } catch (e: Exception) {
                    Timber.e("recorder on android(S) can`t be prepared")
                }
                start()
                updateRecordingState(RecordingState.Recording)
            }
        }
        val notification = NotificationCompat.Builder(this, "recorder_channel")
            .setColorized(true)
            .setContentTitle("Voice Recorder")
            .setContentText("Recording...")
            .build()
        startForeground(1, notification)
    }

    fun stopRecording(onStopRecording: () -> Unit) {
        serviceScope.launch {
            recorder.apply {
                stop()
                reset()
                updateRecordingState(RecordingState.Idle)
                delay(500)
                onStopRecording()
                Timber.e("stopped recording")
            }
        }
    }

    private fun pauseRecording() {
        serviceScope.launch {
            recorder.apply {
                pause()
                updateRecordingState(RecordingState.Paused)
            }
        }
    }

    private fun resumeRecording() {
        serviceScope.launch {
            recorder.apply {
                resume()
                updateRecordingState(RecordingState.Recording)
            }
        }
    }

    private fun releaseResources() {
        serviceScope.launch {
            recorder.apply {
                release()
                updateRecordingState(RecordingState.Idle)
            }
            job.cancel()
        }
    }

    private fun updateRecordingState(status: RecordingState) {
        recordingState = status
        Timber.e(recordingState.toString())
    }

    fun setRecordingTimer(timeMillis: Long) {
        recordingStartTimeMillis = timeMillis
    }

    fun getRecordingStartMillis(): Long {
        return recordingStartTimeMillis
    }

    companion object {
        enum class RecordingState {
            Recording,
            Paused,
            Idle
        }
    }

}