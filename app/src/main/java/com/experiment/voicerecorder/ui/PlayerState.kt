package com.experiment.voicerecorder.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun rememberPlayerState(
    future: ListenableFuture<MediaBrowser>,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): PlayerState {
    val lifecycleOwner = LocalLifecycleOwner.current
    var progress by remember {
        mutableLongStateOf(0L)
    }
    var currentDuration by remember {
        mutableLongStateOf(0L)
    }
    var isPlaying by remember {
        mutableStateOf(false)
    }
    PlayerStateEffect(
        future = future,
        lifecycleOwner = lifecycleOwner,
        progress = {
            progress = it
            Timber.e("pp$it")
        },
        currentDuration = {
            currentDuration = it
        },
        isVoicePlaying = {
            Timber.e("isplayinggg$it")
            isPlaying = it
        }
    )
    return remember(isPlaying) {
        PlayerState(
            isPlaying = isPlaying,
            progress = progress,
            duration = currentDuration,
            coroutineScope = coroutineScope
        )
    }
}

@Stable
class PlayerState(
    isPlaying: Boolean,
    progress: Long,
    duration: Long,
    coroutineScope: CoroutineScope,
) {
    var isVoicePlaying = flow {
        emit(isPlaying)
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(1_000),
        initialValue = false)
    var progress = if (isVoicePlaying.value) progress.toFloat() else 0f
    var voiceDuration = duration.toFloat()

    init {
        Timber.e("init state")
    }


}

@Composable
fun PlayerStateEffect(
    future: ListenableFuture<MediaBrowser>,
    lifecycleOwner: LifecycleOwner,
    progress: (Long) -> Unit,
    currentDuration: (Long) -> Unit,
    isVoicePlaying: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event.targetState == Lifecycle.State.STARTED) {
                future.addListener(
                    {
                        future.get().apply {
                            isVoicePlaying(isPlaying)
                            addListener(
                                object : Player.Listener {
                                    override fun onPlaybackStateChanged(
                                        playbackState: Int,
                                    ) {
                                        when (playbackState) {
                                            Player.STATE_IDLE -> {
                                                isVoicePlaying(isPlaying)
                                                currentDuration(duration)
                                            }

                                            Player.STATE_ENDED -> {
                                                isVoicePlaying(isPlaying)
                                                progress(0L)
                                                currentDuration(0L)
                                            }

                                            Player.STATE_BUFFERING -> {

                                            }

                                            Player.STATE_READY -> {
                                                isVoicePlaying(isPlaying)
                                                currentDuration(duration)
                                                scope.launch {
                                                    while (isPlaying) {
                                                        delay(1_000L)
                                                        progress(currentPosition)
                                                        Timber.e("sp:$currentPosition")
                                                    }
                                                }
                                            }
                                        }
                                        super.onPlaybackStateChanged(
                                            playbackState
                                        )
                                    }

                                    override fun onPlayWhenReadyChanged(
                                        playWhenReady: Boolean,
                                        reason: Int,
                                    ) {
                                        Timber.e("play when ready:$playWhenReady")
                                        isVoicePlaying(isPlaying)
                                        super.onPlayWhenReadyChanged(
                                            playWhenReady,
                                            reason
                                        )
                                    }

                                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                                        isVoicePlaying(isPlaying)
                                        if (isPlaying.not())
                                            progress(0L)
                                        super.onIsPlayingChanged(isPlaying)
                                        Timber.e("is playing chnage:$isPlaying")
                                    }


                                    override fun onPlayerError(error: PlaybackException) {
                                        super.onPlayerError(error)
                                        Timber.e(error.message)
                                        stop()
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
}