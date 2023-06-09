package com.experiment.voicerecorder.compose

import androidx.compose.runtime.Composable
import com.experiment.voicerecorder.VoiceRecorderPermissionsHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@Composable
fun MainScreen(content: @Composable () -> Unit) {
    VoiceRecorderPermissionsHandler {
        content()
    }
}


