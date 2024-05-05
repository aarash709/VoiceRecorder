package com.recorder.feature.playlist

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.recorder.service.RecorderService
import com.recorder.service.RecorderService.Companion.RecordingState

@Composable
fun rememberRecorderState(context: Context = LocalContext.current): RecorderState {
    var recorderService: RecorderService? by remember {
        mutableStateOf(null)
    }
    var isRecorderServiceBound by remember {
        mutableStateOf(false)
    }
    var isRecording by rememberSaveable {
        mutableStateOf(false)
    }
    var lastRecordTime by rememberSaveable {
        mutableLongStateOf(0)
    }
    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
                recorderService = (binder as RecorderService.LocalBinder).getRecorderService()
                isRecorderServiceBound = true
                isRecording =
                    recorderService?.recordingState == RecordingState.Recording
                lastRecordTime = recorderService?.recordingStartTimeMillis ?: 0
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                isRecording =
                    recorderService?.recordingState == RecordingState.Recording
                isRecorderServiceBound = false
            }
        }
    }
    DisposableEffect(key1 = LocalLifecycleOwner.current) {
        if (!isRecorderServiceBound) {
            Intent(context.applicationContext, RecorderService::class.java).apply {
                context.bindService(this, connection, Context.BIND_AUTO_CREATE)
            }
        }
        onDispose {
            if (isRecorderServiceBound) {
                context.unbindService(connection)
            }
        }
    }
    val recorderState = remember(
        connection,
        recorderService,
        isRecording,
        lastRecordTime,
        context
    ) {
        RecorderState(recorderService, context)
    }
    return recorderState
}

@Stable
class RecorderState(
    private val service: RecorderService? = null,
    private val context: Context,
) {
    val isRecording = service?.recordingState == RecordingState.Recording
    val lastRecordTime = service?.recordingStartTimeMillis ?: 0
    fun startRecording() {
        service?.startRecording(context)
    }
    fun stopRecording() {
        service?.stopRecording {  }
    }
}