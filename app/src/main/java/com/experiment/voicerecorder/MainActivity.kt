package com.experiment.voicerecorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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
        setContent {
            VoiceRecorderTheme {
                RecorderApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {

    }
}
