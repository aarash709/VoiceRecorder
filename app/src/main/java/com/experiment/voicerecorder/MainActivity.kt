package com.experiment.voicerecorder

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import androidx.navigation.compose.rememberNavController
import com.experiment.voicerecorder.ui.MainScreen
import com.experiment.voicerecorder.ui.VoiceRecorderNavigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import com.recorder.service.PlayerService
import dagger.hilt.android.AndroidEntryPoint
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
                    Box(modifier = Modifier) {
                        var isVoicePlaying by remember() {
                            mutableStateOf(false)
                        }

                        val lifecycleOwner = LocalLifecycleOwner.current
                        val context = LocalContext.current
                        val scope = rememberCoroutineScope()
                        DisposableEffect(lifecycleOwner) {
                            val observer = LifecycleEventObserver { _, event ->
                                if (event.targetState == Lifecycle.State.STARTED) {
                                    browserFuture.addListener(
                                        {
                                            browserFuture.get().apply {
                                                isVoicePlaying = isPlaying
                                                addListener(
                                                    object : Player.Listener {
                                                        override fun onEvents(
                                                            player: Player,
                                                            events: Player.Events,
                                                        ) {
                                                            super.onEvents(player, events)
                                                        }

                                                        override fun onPlaybackStateChanged(
                                                            playbackState: Int,
                                                        ) {
                                                            when (playbackState) {
                                                                Player.STATE_IDLE -> {
                                                                    isVoicePlaying = isPlaying
                                                                }
                                                                Player.STATE_ENDED->{
                                                                    isVoicePlaying = isPlaying
                                                                }

                                                                Player.STATE_BUFFERING -> {

                                                                }

                                                                Player.STATE_READY -> {

                                                                }
                                                            }
                                                            super.onPlaybackStateChanged(
                                                                playbackState
                                                            )
                                                        }

                                                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                                                            super.onIsPlayingChanged(isPlaying)
                                                            isVoicePlaying = isPlaying
                                                            Timber.e("isplaying chnage$isPlaying")
                                                        }

                                                        override fun onPlayerError(error: PlaybackException) {
                                                            super.onPlayerError(error)
                                                            Timber.e(error.message)
                                                            browser?.stop()
                                                        }

                                                    })
                                            }

                                        }, MoreExecutors.directExecutor()
                                    )
                                }
                            }
                            lifecycleOwner.lifecycle.addObserver(observer)
                            onDispose {
                                lifecycleOwner.lifecycle.removeObserver(observer)
                            }
                        }
                            VoiceRecorderNavigation(
                                modifier = Modifier,
                                navController = navState,
                                voices = listOf(),
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
                                        pause()
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
