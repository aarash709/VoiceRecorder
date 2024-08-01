package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val PLAYLIST_ROUTE = "PLAYLIST_ROUTE"

fun NavController.toPlaylist(navOptions: NavOptions? = null) {
    navigate(PLAYLIST_ROUTE, navOptions)
}

fun NavGraphBuilder.playlist(
    onNavigateToSettings: () -> Unit,
    onNavigateToRecorder: () -> Unit,
    onBackPressed: () -> Unit,
) {
    composable(
        route = PLAYLIST_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                else -> slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400),
                    initialOffset = { it / 3 }
                )
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                else -> slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400),
                    targetOffset = { it / 3 }
                )
            }
        }) {

        Playlist(
            onNavigateToSettings = { onNavigateToSettings() },
            onNavigateToRecorder = { onNavigateToRecorder() },
            onBackPressed = { onBackPressed() },
        )
    }
}