package com.experiment.voicerecorder

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import androidx.navigation.compose.rememberNavController
import com.experiment.voicerecorder.ui.MainScreen
import com.experiment.voicerecorder.ui.VoiceRecorderNavigation
import com.experiment.voicerecorder.ui.rememberPlayerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.common.util.concurrent.ListenableFuture
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import com.recorder.service.PlayerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber

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
                val navState = rememberNavController()
                MainScreen {
                    val playerState = rememberPlayerState(future = browserFuture)
                    Box(modifier = Modifier) {
                        val isVoicePlaying = playerState.isVoicePlaying
                        var progress by remember() {
                            mutableLongStateOf(0)
                        }
                        val voiceDuration = playerState.voiceDuration

                        LaunchedEffect(key1 = progress, isVoicePlaying) {
                            if (isVoicePlaying)
                                browser?.run {
                                    while (true) {
                                        delay(1_000)
                                        progress = currentPosition
                                        Timber.e("p$progress")
                                    }
                                }
                            else
                                progress = playerState.progress
                        }
                        VoiceRecorderNavigation(
                            modifier = Modifier,
                            navController = navState,
                            isPlaying = isVoicePlaying,
                            onPlay = { index, voice ->
                                val metadata = MediaMetadata.Builder()
                                    .setTitle(voice.title)
                                    .setIsPlayable(true).build()
                                val mediaitem = MediaItem.Builder()
                                    .setMediaMetadata(metadata)
                                    .setUri(voice.path)
                                    .setMediaId(voice.title)
                                    .build()
                                browser?.run {
                                    setMediaItem(mediaitem)
                                    play()
                                }
                            },
                            onStop = {
                                browser?.run {
                                    stop()
                                }
                            },
                            progress = progress.toFloat(),
                            duration = voiceDuration.toFloat(),
                            onProgressChange = { currentPosition ->
                                browser?.run {
                                    seekTo(currentPosition.toLong())
                                }
                            }
                        )
                    }
                }
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
