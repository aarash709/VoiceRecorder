package com.experiment.voicerecorder.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.media3.session.MediaBrowser
import com.experiment.voicerecorder.VoiceRecorderPermissionsHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalPermissionsApi
@Composable
fun RecorderApp(
    mediaBrowser: MediaBrowser?,
    playerState: PlayerState,
) {
    VoiceRecorderPermissionsHandler {
        VoiceRecorderNavigation(
            mediaBrowser = mediaBrowser,
            playerState = playerState
        )
    }
}


