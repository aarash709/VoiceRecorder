package com.experiment.voicerecorder.service.record

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RecordingService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }
}