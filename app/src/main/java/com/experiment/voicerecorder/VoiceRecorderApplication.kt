package com.experiment.voicerecorder

import android.app.Application
import timber.log.Timber

class VoiceRecorderApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}