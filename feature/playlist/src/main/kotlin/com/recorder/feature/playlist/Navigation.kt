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

        Playlist(
            onBackPressed = { onBackPressed() },
        )
    }
}