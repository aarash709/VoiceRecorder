package com.experiment.voicerecorder

import android.content.ComponentName
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
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.experiment.voicerecorder.ui.RecorderApp
import com.experiment.voicerecorder.ui.rememberPlayerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.common.util.concurrent.ListenableFuture
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import com.recorder.service.PlayerService
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val browser
        get() = if (browserFuture.isDone) browserFuture.get() else null
    private val mediaItems = mutableListOf<MediaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoiceRecorderTheme {
                val playerState = rememberPlayerState(future = browserFuture)
                val isVoicePlaying = playerState.isVoicePlaying
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
                    mediaBrowser = browser,
                    playerState = playerState
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(
            this,
            ComponentName(this, PlayerService::class.java)
        )
        browserFuture = MediaBrowser.Builder(this, sessionToken).buildAsync()
    }

    override fun onStop() {
        super.onStop()
        MediaBrowser.releaseFuture(browserFuture)
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {

    }
}
