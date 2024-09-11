package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object PlaylistRoute

fun NavController.toPlaylist(navOptions: NavOptions? = null) {
    navigate(PlaylistRoute, navOptions)
}

fun NavGraphBuilder.playlist(
    onNavigateToSettings: () -> Unit,
    onNavigateToRecorder: () -> Unit,
    onBackPressed: () -> Unit,
) {
    composable<PlaylistRoute>(
        enterTransition = {
//            when (initialState.destination.route) {
//                "RECORDER_ROUTE" -> {
//                    fadeIn(initialAlpha = 0.5f) + scaleIn(initialScale = 0.9f)
//                }

//                else ->
                            slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400),
                    initialOffset = { it / 3 }
                )
//            }
        },
        exitTransition = {
//            when (targetState.destination.route) {
//                "RECORDER_ROUTE" -> {
//                    fadeOut(targetAlpha = 0f) + scaleOut(targetScale = 0.9f)
//                }

//                else ->
            slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400),
                    targetOffset = { it / 3 }
                )
//            }
        }) {

        Playlist(
            onNavigateToSettings = { onNavigateToSettings() },
            onNavigateToRecorder = { onNavigateToRecorder() },
            onBackPressed = { onBackPressed() },
        )
    }
}