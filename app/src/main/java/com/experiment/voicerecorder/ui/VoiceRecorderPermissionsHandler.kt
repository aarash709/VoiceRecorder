package com.experiment.voicerecorder.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@ExperimentalPermissionsApi
@Composable
fun VoiceRecorderPermissionsHandler(
    content: @Composable () -> Unit,
) {
    val permissionList: List<String> =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.RECORD_AUDIO
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
            )
        }
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionState = rememberMultiplePermissionsState(
        permissions = permissionList
    )
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                permissionState.allPermissionsGranted
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    permissionState.permissions.forEach { permission ->
        when (permission.permission) {
            Manifest.permission.READ_MEDIA_AUDIO ->
                when {
                    permission.status.isGranted -> content()
                    !permission.status.isGranted -> Text(text = "no access to audio files")
                    permission.status.shouldShowRationale ->
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "accept write ratianale",
                                color = MaterialTheme.colors.onSurface
                            )
                            Button(onClick = { permission.launchPermissionRequest() }) {
                                Text(
                                    text = "grant permission",
                                    color = MaterialTheme.colors.onSurface
                                )
                            }
                        }
                    permission.permanentlyDenied() -> Text(
                        text = "Write permission was permanently denied. You can enable it in the app settings.",
                        color = MaterialTheme.colors.onSurface
                    )
                }

            Manifest.permission.RECORD_AUDIO ->
                when {
                    permission.status.isGranted -> content()
                    permission.status.shouldShowRationale ->
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "accept record ratianale",
                                color = MaterialTheme.colors.onSurface
                            )
                            Button(onClick = { permission.launchPermissionRequest() }) {
                                Text(
                                    text = "grant permission",
                                    color = MaterialTheme.colors.onSurface
                                )
                            }
                        }
                    permission.permanentlyDenied() ->
                        Text(
                            text = "Record audio permission was permanently denied. You can enable it in the app settings.",
                            color = MaterialTheme.colors.onSurface
                        )
                }
        }
    }
}

// ext
@ExperimentalPermissionsApi
fun PermissionState.permanentlyDenied(): Boolean {
    return !status.isGranted && !status.shouldShowRationale
}
