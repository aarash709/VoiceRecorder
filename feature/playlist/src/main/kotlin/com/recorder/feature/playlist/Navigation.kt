package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val RECORDING_ROUTE = "RECORDING_ROUTE"

fun NavController.toRecordings(navOptions: NavOptions? = null) {
    navigate(RECORDING_ROUTE, navOptions)
}

fun NavGraphBuilder.playlist(
    onNavigateToSettings: () -> Unit,
    onBackPressed: () -> Unit,
) {
    composable(
        route = RECORDING_ROUTE,
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
            onBackPressed = { onBackPressed() },
        )
    }
}