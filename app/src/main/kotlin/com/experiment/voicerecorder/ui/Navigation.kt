package com.experiment.voicerecorder.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.feature.playlist.RECORDING_ROUTE
import com.recorder.feature.playlist.playlist

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun VoiceRecorderNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = RECORDING_ROUTE
    ) {
        playlist(
                onBackPressed = { navController.popBackStack() },
        )
    }
}