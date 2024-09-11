package com.experiment.voicerecorder.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.recorder.core.designsystem.theme.LocalSharedTransitionScope
import com.recorder.feature.playlist.PlaylistRoute
import com.recorder.feature.playlist.playlist
import com.recorder.feature.playlist.toPlaylist
import com.recorder.feature.record.RecorderRoute
import com.recorder.feature.record.recorder
import com.recorder.feature.record.toRecorder
import com.recorder.feature.settings.settings
import com.recorder.feature.settings.toSettings

@OptIn(
    ExperimentalSharedTransitionApi::class
)
@Composable
fun VoiceRecorderNavigation(
    navController: NavHostController = rememberNavController(),
) {
    SharedTransitionLayout {
        CompositionLocalProvider(value = LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController,
                startDestination = PlaylistRoute
            ) {
                playlist(
                    onNavigateToSettings = { navController.toSettings() },
                    onNavigateToRecorder = {
                        navController.toRecorder(navOptions = navOptions {
                            popUpTo(route = PlaylistRoute, popUpToBuilder = { inclusive = true })
                        })
                    },
                    onBackPressed = { navController.popBackStack() },
                )
                recorder(onNavigateToPlaylist = {
                    navController.toPlaylist(
                        navOptions = navOptions {
                            popUpTo(route = RecorderRoute, popUpToBuilder = { inclusive = true })
                        }
                    )
                })
                settings(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}