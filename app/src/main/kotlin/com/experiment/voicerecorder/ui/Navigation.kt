package com.experiment.voicerecorder.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.core.designsystem.theme.LocalSharedTransitionScope
import com.recorder.feature.playlist.PLAYLIST_ROUTE
import com.recorder.feature.playlist.playlist
import com.recorder.feature.playlist.toPlaylist
import com.recorder.feature.record.RECORDER_ROUTE
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
                startDestination = PLAYLIST_ROUTE
            ) {
                playlist(
                    onNavigateToSettings = { navController.toSettings() },
                    onNavigateToRecorder = {
                        navController.toRecorder(navOptions = navOptions {
                            launchSingleTop = true
                            popUpTo(RECORDER_ROUTE)
                        })
                    },
                    onBackPressed = { navController.popBackStack() },
                )
                recorder(onNavigateToPlaylist = { navController.toPlaylist() })
                settings(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}