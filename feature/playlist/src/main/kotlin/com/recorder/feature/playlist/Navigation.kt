package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
			slideIntoContainer(
				towards = AnimatedContentTransitionScope.SlideDirection.Right,
				animationSpec = tween(400),
				initialOffset = { it / 3 }
			)
		},
		exitTransition = {
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