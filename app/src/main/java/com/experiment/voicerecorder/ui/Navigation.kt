package com.experiment.voicerecorder.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaBrowser
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.feature.playlist.recordings
import com.recorder.feature.playlist.toRecordings
import com.recorder.feature.record.RECORDER_ROUTE
import com.recorder.feature.record.recorder
import timber.log.Timber

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
    mediaBrowser: MediaBrowser?,
    playerState: PlayerState,
//    progress: Float,
//    duration: Float,
//    onProgressChange: (Float) -> Unit,
//    isPlaying: Boolean,
//    onPlay: (Int, Voice) -> Unit,
//    onStop: () -> Unit,
//    onPlayPause:()->Unit,
) {
    val isPlaying = playerState.isVoicePlaying.collectAsStateWithLifecycle()
    NavHost(
        navController = navController,
        startDestination = RECORDER_ROUTE
    ) {
        recorder(onListButtonClick = {
            navController.toRecordings()
        })

        recordings(
            isPlaying = isPlaying.value,
            onPlay = { _, voice ->
                Timber.e("onplay")
                val metadata = MediaMetadata.Builder()
                    .setTitle(voice.title)
                    .setIsPlayable(true).build()
                val mediaitem = MediaItem.Builder()
                    .setMediaMetadata(metadata)
                    .setUri(voice.path)
                    .setMediaId(voice.title)
                    .build()
                if (mediaBrowser == null) {
                    Timber.e("browsernull")
                }
                mediaBrowser?.run {
                    Timber.e(mediaitem.mediaId)
                    setMediaItem(mediaitem)
                    play()
                }
            },
            onStop = {
                mediaBrowser?.run {
                    stop()
                }
            },
            onBackPressed = { navController.popBackStack() },
            progress = playerState.progress,
            duration = playerState.voiceDuration,
            onProgressChange = { newProgress ->
                mediaBrowser?.run {
                    seekTo(newProgress.toLong())
                }
            }
        )
    }
}