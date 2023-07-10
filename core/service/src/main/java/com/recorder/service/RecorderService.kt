package com.recorder.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import com.core.common.Storage
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecorderService : Service() {

    private lateinit var recorder: MediaRecorder
    private lateinit var storage: Storage

    override fun onCreate() {
        super.onCreate()
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            MediaRecorder(this)
        else
            MediaRecorder()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecording(context = this)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun onRecord(){

    }

    private fun startRecording(context: Context) {
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
//                Timber.e("recorder on android(S) can`t be prepared")
            }
            start()
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