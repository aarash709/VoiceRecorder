package com.experiment.voicerecorder

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.SessionToken
import androidx.navigation.compose.rememberNavController
import com.experiment.voicerecorder.ui.MainScreen
import com.experiment.voicerecorder.ui.VoiceRecorderNavigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.MoreExecutors
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import com.recorder.service.PlayerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.internal.immutableListOf
import timber.log.Timber

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var playerService: PlayerService
    private var isPlayerServiceBound = MutableStateFlow(false)
    private lateinit var browser: MediaBrowser
    private val mediaItems = mutableListOf<MediaItem>()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Timber.e("service connection")
            val binder = service as PlayerService.LocalBinder
            playerService = binder.getService()
            isPlayerServiceBound.update { true }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isPlayerServiceBound.update { false }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Intent(this, PlayerService::class.java).also {
//            bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
//        }
        setContent {
            VoiceRecorderTheme {
                val navState = rememberNavController()
                val isPlaying =
                    if (isPlayerServiceBound.collectAsState().value)
                        playerService.isPlaying.collectAsState().value
                    else false
                val voices = if (isPlayerServiceBound.collectAsState().value)
                    playerService.voices.collectAsState().value
                else
                    listOf()

                MainScreen {
                    Box(modifier = Modifier) {
                        VoiceRecorderNavigation(
                            modifier = Modifier,
                            navController = navState,
                            voices = mediaItems,
                            isPlaying = isPlaying,
                            onPlay = { index, voice ->
                                val mediaItem = MediaItem.fromUri(voice.path)
                                if (browser.isConnected) {
                                    Timber.e("${browser.isConnected}")
                                    browser.run {
                                        setMediaItem(mediaItem)
                                        prepare()
                                        play()
                                    }
                                }
                                if (isPlayerServiceBound.value) {
                                    playerService.onPlay(voice, index)
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
        val mediaBrowser = MediaBrowser.Builder(this, sessionToken).buildAsync()
        mediaBrowser.addListener(
            {
                browser = mediaBrowser.get()
                val root = browser.getLibraryRoot(null)
                root.addListener(
                    {
                        val rootMediaItem = root.get().value!!
                        val childrenFuture = browser.getChildren(
                            rootMediaItem.mediaId,
                            0,
                            Int.MAX_VALUE,
                            null
                        )
                        childrenFuture.addListener(
                            {
                                val mediaItems = childrenFuture.get().value!!
                                mediaItems.addAll(mediaItems)
                            },
                            MoreExecutors.directExecutor()
                        )

                    },
                    MoreExecutors.directExecutor()
                )
//                val items = browser.getChildren(
//                    "",
//                    0,
//                    Int.MAX_VALUE,
//                    null
//                ).get().value!!
//                mediaItems.toMutableList().addAll(items)
//                Timber.e("item: ${items.any()}")
            },
            MoreExecutors.directExecutor()
//            ContextCompat.getMainExecutor(this)

        )
    }

    override fun onDestroy() {
        super.onDestroy()
//        unbindService(serviceConnection)
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {

    }
}
