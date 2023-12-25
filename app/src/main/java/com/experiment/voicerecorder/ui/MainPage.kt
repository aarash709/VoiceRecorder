package com.experiment.voicerecorder.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import com.experiment.voicerecorder.VoiceRecorderPermissionsHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalPermissionsApi
@Composable
fun RecorderApp() {
    VoiceRecorderPermissionsHandler {
        VoiceRecorderNavigation()
    }
}


