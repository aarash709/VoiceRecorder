package com.experiment.voicerecorder.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.core.designsystem.theme.LocalSharedTransitionScope
import com.recorder.feature.playlist.RECORDING_ROUTE
import com.recorder.feature.playlist.playlist
import com.recorder.feature.settings.settings
import com.recorder.feature.settings.toSettings

@OptIn(
    ExperimentalSharedTransitionApi::class,
    ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun VoiceRecorderNavigation(
    navController: NavHostController = rememberNavController(),
) {
    SharedTransitionLayout {
        CompositionLocalProvider(value = LocalSharedTransitionScope provides this) {
            NavHost(
                navController = navController,
                startDestination = RECORDING_ROUTE
            ) {
                playlist(
                    onNavigateToSettings = { navController.toSettings() },
                    onBackPressed = { navController.popBackStack() },
                )
                settings(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}