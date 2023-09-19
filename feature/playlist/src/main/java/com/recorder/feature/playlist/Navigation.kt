package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.core.common.model.Voice
import kotlinx.coroutines.launch

const val RECORDING_ROUTE = "RECORDING_ROUTE"

fun NavController.toRecordings(navOptions: NavOptions? = null) {
    navigate(RECORDING_ROUTE, navOptions)
}

fun NavGraphBuilder.recordings(
    voices: List<MediaItem>,
    isPlaying: Boolean,
    onPlay: (Int, Voice) -> Unit,
    onStop: () -> Unit,
    onBackPressed: () -> Unit,
) {
    composable(
        route = RECORDING_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                else -> slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(400)
                )
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                else -> slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(400)
                )
            }
        }) {
        var voiceList by remember {
            mutableStateOf(listOf<Voice>())
        }
        LaunchedEffect(key1 = voices, block = {
            launch {
                voiceList = voices.map {
                    Voice(
                        title = it.mediaMetadata.title.toString(),
                        path = it.localConfiguration?.uri.toString(),
                        isPlaying = false,
                        duration = "",
                        recordTime = ""
                    )
                }
            }
        })
        Playlist(
            isPlaying = isPlaying,
            voices = voiceList,
            onPlayPause = { },
            onStop = { onStop() },
            onVoiceClicked = { i, voice ->
                onPlay(i, voice)
            },
            onBackPressed = { onBackPressed() }
        )
    }
}