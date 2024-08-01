package com.recorder.feature.record

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val RECORDER_ROUTE = "RECORDER_ROUTE"

fun NavController.toRecorder(navOptions: NavOptions? = null) {
    navigate(RECORDER_ROUTE, navOptions)
}

fun NavGraphBuilder.recorder(
    onNavigateToPlaylist: () -> Unit
){
    composable(
        route = RECORDER_ROUTE,
        enterTransition = {
            fadeIn(initialAlpha = 0.5f) + scaleIn(initialScale = 0.9f)
//            slideIntoContainer(
//                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
//                    animationSpec = tween(350),
//                    initialOffset = { it/3 }
//                )
        },
        exitTransition = {
            fadeOut(targetAlpha = 0f) + scaleOut(targetScale = 0.9f)            /*when (targetState.destination.route) {
                else -> slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(350),
                    targetOffset = { (it/3) }
                )
            }*/
        }){
        Record(
            onNavigateToPlaylist = { onNavigateToPlaylist() }
        )
    }
}