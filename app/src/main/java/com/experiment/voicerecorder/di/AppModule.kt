package com.experiment.voicerecorder.di

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @RequiresApi(Build.VERSION_CODES.S)
    @Singleton
    @Provides
    fun providesMediaRecorder(
        @ApplicationContext context: Context
    ) = MediaRecorder(context)
}