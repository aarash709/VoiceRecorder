package com.experiment.voicerecorder

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.rememberNavController
import com.core.common.Storage
import com.experiment.voicerecorder.ui.MainScreen
import com.experiment.voicerecorder.ui.VoiceRecorderNavigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.internal.ViewUtils.getChildren
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import com.recorder.service.PlayerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var playerService: PlayerService
    private var isPlayerServiceBound = MutableStateFlow(false)
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val browser
        get() = if (browserFuture.isDone) browserFuture.get() else null
    private val mediaItems = mutableListOf<MediaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoiceRecorderTheme {
                val navState = rememberNavController()
//                val isPlaying =
//                    if (isPlayerServiceBound.collectAsState().value)
//                        playerService.isPlaying.collectAsState().value
//                    else false
                val voices = if (browser != null){
                    Storage().getVoices(this)!!.map {
                        MediaItem.fromUri(it.path)
                    }
                }
                else {
                    listOf()
                }

                MainScreen {
                    Box(modifier = Modifier) {
                        VoiceRecorderNavigation(
                            modifier = Modifier,
                            navController = navState,
                            voices = voices,
                            isPlaying = /*isPlaying*/false,
                            onPlay = { index, voice ->
                                val mediaItem =
                                    MediaItem.fromUri("/storage/emulated/0/Android/data/com.experiment.voicerecorder/files/230705_142853.m4a")
                                if (browser?.isConnected!!) {
                                    Timber.e("connected:${browser?.isConnected}")
                                    browser?.run {
                                        setMediaItem(mediaItem)
                                        prepare()
                                        play()
                                        Timber.e("path: ${mediaItem.localConfiguration?.uri}")
                                        Timber.e("currt itm:${browser?.currentMediaItem?.mediaId}")
                                        Timber.e("${this.isPlaying}")
                                    }
                                    Timber.e("islive: ${browser?.currentMediaItem?.mediaMetadata?.title}")
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
        browserFuture = MediaBrowser.Builder(this, sessionToken).buildAsync()
        browserFuture.addListener(
            {
                val mediaBrowser = browserFuture.get()
                val rootFuture = mediaBrowser.getLibraryRoot(null)
                Timber.e("${mediaBrowser.mediaItemCount}")
                rootFuture.addListener(
                    {
                        val rootMediaItem = rootFuture.get().value!!
                        Timber.e("uri root${rootMediaItem.mediaMetadata.artworkUri}")
                        mediaItems.add(rootMediaItem)

                        getChildren(rootMediaItem)
                    },
                    ContextCompat.getMainExecutor(this)
                )
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getChildren(mediaItem: MediaItem) {
        val childrenFuture =
            browser?.getChildren(
                mediaItem.mediaId,
                0,
                Int.MAX_VALUE,
                null
            )
        childrenFuture?.addListener(
            {
                val childItems = childrenFuture.get().value!!
                mediaItems.clear()
                mediaItems.addAll(childItems)
            },
            MoreExecutors.directExecutor()
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {

    }
}
