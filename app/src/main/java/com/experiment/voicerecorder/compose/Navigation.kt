package com.experiment.voicerecorder.compose

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.core.common.model.Voice
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.feature.playlist.PlaylistScaffold
import com.recorder.feature.record.Record

sealed class Pages(val route: String) {
    object RecordingPage : Pages("RecordingPage")
    object PlayListPage : Pages("PlayListPage")
}

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun VoiceRecorderNavigation(
    modifier: Modifier = Modifier,
    navHost: NavHostController = rememberNavController(),
//    viewModel: MainViewModel = viewModel(),
    recordEnabled: Boolean,
    playlistButtonEnabled: Boolean,
    isPlaying: Boolean,
    timer: String,
    voices: List<Voice>,
    onPlayPause:()->Unit,
    onStop:()->Unit,
    onRecord: () -> Unit,
    onPlay: (Int, Voice) -> Unit,
) {

    NavHost(navController = navHost, startDestination = Pages.RecordingPage.route) {
        composable(Pages.RecordingPage.route) {
            Record()
//            RecodingPage(modifier = modifier,
//                recordEnabled = recordEnabled,
//                recordingTime = timer,
//                playlistButtonEnabled,
//                onRecord = { onRecord() }) {
//                navHost.navigate(Pages.PlayListPage.route) {
//                    popUpTo(Pages.RecordingPage.route)
//                }
//            }
        }
        composable(Pages.PlayListPage.route) {
            PlaylistScaffold(
                voices,
                onPlayPause = onPlayPause,
                onStop = onStop,
                onVoiceClicked = { i, voice ->
                    onPlay(i, voice)
                })
        }
    }
}