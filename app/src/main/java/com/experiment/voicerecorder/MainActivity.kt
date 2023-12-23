package com.experiment.voicerecorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                val playerState = rememberPlayerState()
                var progress by remember() {
                    mutableFloatStateOf(0f)
                }
//                LaunchedEffect(key1 = progress, isVoicePlaying) {
//                    if (isVoicePlaying)
//                        browser?.run {
//                            while (true) {
//                                delay(1_000)
//                                progress = currentPosition.toFloat()
//                                Timber.e("p$progress")
//                            }
//                        }
//                    else
//                        progress = playerState.progress
//                }
                RecorderApp(
                    playerState = playerState
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
//        val sessionToken = SessionToken(
//            this,
//            ComponentName(this, PlayerService::class.java)
//        )
//        browserFuture = MediaBrowser.Builder(this, sessionToken).buildAsync()
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
