package com.experiment.voicerecorder.ui

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@Composable
fun MainScreen(content: @Composable () -> Unit) {
    VoiceRecorderPermissionsHandler {
        content()
    }
}
