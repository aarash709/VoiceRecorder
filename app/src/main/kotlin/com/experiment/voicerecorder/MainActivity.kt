package com.experiment.voicerecorder

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.experiment.voicerecorder.ui.RecorderApp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            ),
            0
        )
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(isDarkTheme) {
                enableEdgeToEdge(
                    navigationBarStyle = if (isDarkTheme) {
                        SystemBarStyle.dark(
                            Color.TRANSPARENT,
                        )
                    } else {
                        SystemBarStyle.light(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                        )
                    }
                )
            }
            VoiceRecorderTheme {
                RecorderApp()
            }
        }
    }
}