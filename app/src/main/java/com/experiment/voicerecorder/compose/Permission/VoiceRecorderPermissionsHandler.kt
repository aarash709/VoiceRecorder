package com.experiment.voicerecorder

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
@Composable
fun VoiceRecorderPermissionsHandler(
    content: @Composable () -> Unit,
) {
    val permissionList: List<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            listOf(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
//              Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
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
        permissions = permissionList)
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                permissionState.launchMultiplePermissionRequest()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    permissionState.permissions.forEach { state ->
        when (state.permission) {
//            Manifest.permission.MANAGE_EXTERNAL_STORAGE ->
//                when {
//                    permission.hasPermission -> {
//                        content()
//                    }
//                    permission.shouldShowRationale -> {
//                        Column(modifier = Modifier.fillMaxSize()) {
//                            Text(text = "accept manage ratianale")
//                            Button(onClick = { permission.launchPermissionRequest() }) {
//                                Text(text = "grant permission")
//                            }
//                        }
//
//                    }
//                    permission.permissionRequested ->
//                        Text(text = "read permission requested")
//
//                    permission.permanentlyDenied() ->
//                        Text(text = "manage storage permission was permanently" +
//                                "denied. You can enable it in the app" +
//                                "settings.")
//                }
//            Manifest.permission.READ_EXTERNAL_STORAGE ->
//                when {
//                    permission.hasPermission -> {
//                        content()
//                    }
//                    permission.shouldShowRationale -> {
//                        Column(modifier = Modifier.fillMaxSize()) {
//                            Text(text = "accept read ratianale")
//                            Button(onClick = { permission.launchPermissionRequest() }) {
//                                Text(text = "grant permission")
//                            }
//                        }
//
//                    }
//                    permission.permissionRequested ->
//                        Text(text = "read permission requested")
//
//                    permission.permanentlyDenied() ->
//                        Text(text = "Read permission was permanently" +
//                                "denied. You can enable it in the app" +
//                                "settings.")
//                }
//            Manifest.permission.WRITE_EXTERNAL_STORAGE ->
//                when {
//                    permission.hasPermission -> {
//                        content()
//                    }
//                    permission.shouldShowRationale -> {
//                        Column(modifier = Modifier.fillMaxSize()) {
//                            Text(text = "accept write ratianale")
//                            Button(onClick = { permission.launchPermissionRequest() }) {
//                                Text(text = "grant permission")
//                            }
//                        }
//
//                    }
//                    permission.permissionRequested ->
//                        Text(text = "write permission requested")
//                    permission.permanentlyDenied() ->
//                        Text(text = "Write permission was permanently" +
//                                "denied. You can enable it in the app" +
//                                "settings.")
//                }
            Manifest.permission.RECORD_AUDIO ->
                when {
                    state.hasPermission -> {
                        content()
                    }
                    state.shouldShowRationale -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(text = "accept record ratianale")
                            Button(onClick = { state.launchPermissionRequest() }) {
                                Text(text = "grant permission")
                            }
                        }

                    }
                    state.permissionRequested ->
                        Text(text = "record permission requested")

                    state.permanentlyDenied() ->
                        Text(text = "Record audio permission was permanently" +
                                "denied. You can enable it in the app" +
                                "settings.")
                }
        }
    }
}


//ext
@ExperimentalPermissionsApi
fun PermissionState.permanentlyDenied(): Boolean {
    return !hasPermission && !shouldShowRationale
}