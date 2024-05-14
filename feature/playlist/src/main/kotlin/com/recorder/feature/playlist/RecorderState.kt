package com.recorder.feature.playlist

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.recorder.service.RecorderService
import com.recorder.service.RecorderService.Companion.RecordingState
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun rememberRecorderState(
    context: Context = LocalContext.current,
    serviceConnection: ServiceConnection,
    recorderService: RecorderService? = null,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    isServiceBound: Boolean,
): RecorderState {
    var isRecording by rememberSaveable {
        mutableStateOf(false)
    }
    var recordingStartTimeSecond by rememberSaveable {
        mutableLongStateOf(0)
    }
    BindServiceEffect(
        connection = serviceConnection,
        context = context,
        isServiceBound = isServiceBound

    )
    val recorderState = remember(
        serviceConnection,
        recorderService,
        isRecording,
        recordingStartTimeSecond,
        context
    ) {
        RecorderState(recorderService, context, coroutineScope)
    }
    return recorderState
}

@Stable
class RecorderState(
    private val service: RecorderService? = null,
    private val context: Context,
    private val scope: CoroutineScope,
) {
    //work in progress 🚧
    var isRecording = mutableStateOf(false)
    val recordingStartTimeSecond = service?.recordingStartTimeMillis ?: 0
    fun onRecord(onStart: (service: RecorderService) -> Unit, onStop: () -> Unit) {
        service?.let { service ->
            val recordingState = service.recordingState
//            isRecording.value = recordingState == RecordingState.Recording
            Timber.e("recording state:? ${isRecording.value}")
            if (recordingState != RecordingState.Recording) {
                Intent(context.applicationContext, RecorderService::class.java).apply {
                    context.startService(this)
                }
                service.startRecording(context)
                service.setRecordingTimer(timeMillis = System.currentTimeMillis().milliseconds.inWholeSeconds)
                isRecording.value = true
                onStart(service)
            } else {
                service.stopRecording {
                    isRecording.value = false
                    onStop()
                }

            }
        }
    }
//    fun startRecording(onStart: (service: RecorderService) -> Unit) {
//        service?.let {
//            val recordingState = service.recordingState
//            isRecording.value = recordingState == RecordingState.Recording
////            Timber.e("recording state:? $isRecording")
//            if (recordingState != RecordingState.Recording) {
//                Intent(context.applicationContext, RecorderService::class.java).apply {
//                    context.startService(this)
//                }
//                service.startRecording(context)
//                service.setRecordingTimer(timeMillis = System.currentTimeMillis().milliseconds.inWholeSeconds)
//                Timber.e("recording state:? $isRecording")
//                onStart(service)
//            }
//        }
//    }

    fun stop(onStop: () -> Unit) {
        service?.let { service ->
            val recordingState = service.recordingState
            isRecording.value = recordingState != RecordingState.Recording
            if (service.recordingState == RecordingState.Recording) {
                service.stopRecording { onStop() }
            }
        }
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
