package com.experiment.voicerecorder.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.media3.session.MediaBrowser
import com.experiment.voicerecorder.PlayerState
import com.experiment.voicerecorder.VoiceRecorderPermissionsHandler
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalPermissionsApi
@Composable
fun RecorderApp(
    playerState: PlayerState,
    mediaBrowser: MediaBrowser? = playerState.browser,
) {
    VoiceRecorderPermissionsHandler {
        VoiceRecorderNavigation(
            mediaBrowser = mediaBrowser,
            playerState = playerState
        )
    }
}


