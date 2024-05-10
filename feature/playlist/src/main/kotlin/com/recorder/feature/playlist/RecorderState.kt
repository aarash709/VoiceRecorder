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
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberRecorderState(context: Context = LocalContext.current,serviceConnection: ServiceConnection): RecorderState {
    var recorderService: RecorderService? by remember {
        mutableStateOf(null)
    }
    var isRecorderServiceBound by remember {
        mutableStateOf(false)
    }
    var isRecording by rememberSaveable {
        mutableStateOf(false)
    }
    var recordingStartTimeSecond by rememberSaveable {
        mutableLongStateOf(0)
    }
//    val connection = remember {
//        object : ServiceConnection {
//            override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
//                recorderService = (binder as RecorderService.LocalBinder).getRecorderService()
//                isRecorderServiceBound = true
//                recorderService?.let { service ->
//                    isRecording =
//                        service.recordingState == RecordingState.Recording
//                    recordingStartTimeSecond =
//                        service.recordingStartTimeMillis.seconds.inWholeSeconds
//                }
//
//            }
//
//            override fun onServiceDisconnected(p0: ComponentName?) {
//                isRecording =
//                    recorderService?.recordingState == RecordingState.Recording
//                isRecorderServiceBound = false
//            }
//        }
//    }
    BindServiceEffect(
        connection = serviceConnection,
        context = context,
        isServiceBound = isRecorderServiceBound
    )
    val recorderState = remember(
        serviceConnection,
        recorderService,
        isRecording,
        recordingStartTimeSecond,
        context
    ) {
        RecorderState(recorderService, context)
    }
    return recorderState
}

@Stable
class RecorderState(
    val service: RecorderService? = null,
    private val context: Context,
) {
    val isRecording = service?.recordingState == RecordingState.Recording
    val recordingStartTimeSecond = service?.recordingStartTimeMillis ?: 0
    fun startRecording() {
        service?.startRecording(context)
    }

    fun stopRecording() {
        service?.stopRecording { }
    }
}

@Composable
fun BindServiceEffect(
    connection: ServiceConnection,
    context: Context? = null,
    isServiceBound: Boolean,
) {
    val ctx = context ?: LocalContext.current
    DisposableEffect(key1 = LocalLifecycleOwner.current) {
        if (!isServiceBound) {
            Intent(ctx, RecorderService::class.java).apply {
                ctx.bindService(this, connection, Context.BIND_AUTO_CREATE)
            }
        }
        onDispose {
            if (isServiceBound) {
                ctx.unbindService(connection)
            }
        }
    }
}
