package com.experiment.voicerecorder.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.feature.playlist.recordings
import com.recorder.feature.playlist.toRecordings
import com.recorder.feature.record.RECORDER_ROUTE
import com.recorder.feature.record.recorder

sealed class Pages(val route: String) {
    object RecordingPage : Pages("RecordingPage")
    object PlayListPage : Pages("PlayListPage")
}

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun VoiceRecorderNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = RECORDER_ROUTE
    ) {
        recorder(onListButtonClick = {
            navController.toRecordings()
        })

        recordings(
            onBackPressed = { navController.popBackStack() },
        )
    }
}