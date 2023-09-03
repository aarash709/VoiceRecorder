package com.experiment.voicerecorder

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
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
                        VoiceRecorderNavigation(
                            modifier = Modifier,
                            navController = navState,
                            voices = listOf(),
                            isPlaying = /*isPlaying*/false,
                            onPlay = { index, voice ->
                                val metadata = MediaMetadata.Builder().setTitle(voice.title)
                                    .setIsPlayable(true).build()
                                val mediaItem = MediaItem.Builder().setMediaMetadata(metadata)
                                    .setUri(voice.path).setMediaId(voice.title).build()
                                if (browser?.isConnected!!) {
                                    browser?.run {
                                        setMediaItem(mediaItem)
                                        prepare()
                                        play()
                                    }
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
            { getRoot() },
            ContextCompat.getMainExecutor(this)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getRoot() {
        val mediaBrowser = browserFuture.get()
        val rootFuture = mediaBrowser.getLibraryRoot(null)
        rootFuture.addListener(
            {
                val rootMediaItem = rootFuture.get().value!!
                mediaItems.add(rootMediaItem)
                getChildren(rootMediaItem)
            },
            ContextCompat.getMainExecutor(this)
        )
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
