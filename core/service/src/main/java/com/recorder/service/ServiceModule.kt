package com.recorder.service

import android.app.NotificationManager
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import com.core.common.Storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Qualifier

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @AndroidSDK31AndUpMediaRecorder
    @RequiresApi(Build.VERSION_CODES.S)
    @Provides
    fun providesMediaRecorderForAndroidS(
        @ApplicationContext context: Context,
    ): MediaRecorder {
        return MediaRecorder(context)

    }
//    @AndroidSDK30MediaRecorder
    @Provides
    fun providesMediaRecorderForAndroidR(): MediaRecorder {
        return MediaRecorder()
    }

    @Provides
    fun providesStorage(): Storage {
        return Storage()
    }

    @Provides
    fun providesNotificationManager(
        @ApplicationContext context: Context,
    ): NotificationManager {
        return context.
        getSystemService(NotificationManager::class.java) as NotificationManager
    }
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AndroidSDK31AndUpMediaRecorder()

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AndroidSDK30MediaRecorder()


