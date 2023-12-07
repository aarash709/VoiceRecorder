package com.experiment.voicerecorder.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.core.common.model.Voice
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.feature.playlist.recordings
import com.recorder.feature.playlist.toRecordings
import com.recorder.feature.record.RECORDER_ROUTE
import com.recorder.feature.record.recorder

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun VoiceRecorderNavigation(
    navController: NavHostController = rememberNavController(),
    progress: Float,
    duration: Float,
    onProgressChange: (Float) -> Unit,
    isPlaying: Boolean,
    onPlay: (Int, Voice) -> Unit,
    onStop: () -> Unit,
//    onPlayPause:()->Unit,
) {
    NavHost(
        navController = navController,
        startDestination = RECORDER_ROUTE
    ) {
        recorder(onListButtonClick = {
            navController.toRecordings()
        })
        recordings(
            isPlaying = isPlaying,
            onPlay = { i, voice ->
                onPlay(i, voice)
            },
            onStop = { onStop() },
            onBackPressed = { navController.popBackStack() },
            progress = progress,
            duration = duration,
            onProgressChange = { newProgress ->
                onProgressChange(newProgress)
            }
        )
    }
}
