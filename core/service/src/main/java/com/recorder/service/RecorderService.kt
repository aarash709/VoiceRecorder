package com.recorder.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.core.common.Storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "record" -> {
                startRecording(this)
                Timber.e("record")
            }

            "stop" -> {
                stopRecordingAudio(onStopRecording = {

                })
                Timber.e("stop")
            }

            "pause" -> {
                pauseRecording()
                Timber.e("pause")
            }

            "resume" -> {
                resumeRecording()
                Timber.e("resume")
            }
        }
        return START_STICKY
    }

    private fun pauseRecording() {
        serviceScope.launch {
            recorder.apply {
                pause()
            }
        }
    }

    private fun resumeRecording() {
        serviceScope.launch {
            recorder.apply {
                resume()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseResources()
        Timber.e("recorder service destroyed")
    }

    private fun startRecording(context: Context) {
        serviceScope.launch {
            val path = storage.getPath(context)
            val fileName = generateFileName()
            val file = File(path, fileName)
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.path)
                try {
                    prepare()
                } catch (e: Exception) {
                    Timber.e("recorder on android(S) can`t be prepared")
                }
                start()
            }
        }
        val notification = NotificationCompat.Builder(this, "recorder_channel")
            .setColorized(true)
            .setContentTitle("Voice Recorder")
            .setContentText("Recording...")
            .build()
        startForeground(1, notification)
    }

    private fun stopRecordingAudio(onStopRecording: () -> Unit) {
        serviceScope.launch {
            recorder.apply {
                stop()
                reset()
                onStopRecording()
                Timber.e("stopped recording")
            }
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun releaseResources() {
        serviceScope.launch {
            recorder.apply {
                release()
            }
            job.cancel()
        }
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
}