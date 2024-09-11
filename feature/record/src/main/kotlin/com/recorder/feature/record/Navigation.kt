package com.recorder.feature.record

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object RecorderRoute

fun NavController.toRecorder(navOptions: NavOptions? = null) {
    navigate(RecorderRoute, navOptions)
}

fun NavGraphBuilder.recorder(
    onNavigateToPlaylist: () -> Unit
){
    composable<RecorderRoute>(
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400),
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400),
            )
        }){
        Record(
            onNavigateToPlaylist = { onNavigateToPlaylist() }
        )
    }
}