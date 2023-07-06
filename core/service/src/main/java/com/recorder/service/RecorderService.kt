package com.recorder.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RecorderService :Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

}