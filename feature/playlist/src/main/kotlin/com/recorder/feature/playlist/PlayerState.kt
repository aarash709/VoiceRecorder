package com.recorder.feature.playlist

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.core.common.doubleDigitFormat
import com.core.common.getMinutes
import com.core.common.getSeconds
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.recorder.service.PlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@Composable
fun rememberPlayerState(): PlayerState {
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var progress by remember {
        mutableFloatStateOf(0f)
    }
    var currentDuration by remember {
        mutableFloatStateOf(0f)
    }
    var isPlaying by remember {
        mutableStateOf(false)
    }
    var browser by remember {
        mutableStateOf<MediaBrowser?>(null)
    }
    val playerState =
        remember(isPlaying, progress, currentDuration, browser, scope) {
            PlayerState(
                browser = browser,
                isPlaying = isPlaying,
                progress = progress,
                duration = currentDuration,
                coroutineScope = scope
            )
        }
    PlayerStateEffect(
        key1 = lifecycleOwner,
        key2 = "",
        onGetBrowser = {
            browser = it
        },
        progress = { currentPositionMillis ->
//            val seconds = getSeconds(currentPositionMillis).doubleDigitFormat()
//            val minutes = getMinutes(currentPositionMillis).doubleDigitFormat()
//            progress = "$minutes:$seconds"
            progress = currentPositionMillis.seconds.inWholeSeconds.toFloat()
        },
        currentDuration = {
            currentDuration = it.toFloat()
        },
        isVoicePlaying = {
            isPlaying = it
        }
    )
    return playerState
}

@Stable
class PlayerState(
    val browser: MediaBrowser?,
    isPlaying: Boolean,
    progress: Float,
    duration: Float,
    coroutineScope: CoroutineScope,
) {
    val isVoicePlaying = flow {
        emit(isPlaying)
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )
    val progress = flow {
        emit(progress)
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0f
    )
    val voiceDuration = flow {
        emit(duration)
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0f
    )
}

@Composable
fun PlayerStateEffect(
    key1: Any?,
    key2: Any?,
    onGetBrowser: (MediaBrowser?) -> Unit,
    progress: (Long) -> Unit,
    currentDuration: (Long) -> Unit,
    isVoicePlaying: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var browserFuture by remember {
        mutableStateOf<ListenableFuture<MediaBrowser>?>(null)
    }
    var browser by remember {
        mutableStateOf<MediaBrowser?>(null)
    }
    LifecycleStartEffect(key1 = key1, key2 = key2) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlayerService::class.java)
        )
        browserFuture =
            MediaBrowser.Builder(context, sessionToken).buildAsync().apply {
                addListener({
                    browser = if (browserFuture!!.isDone) browserFuture?.get() else null
                    onGetBrowser(browser)
                    browser?.apply {
                        isVoicePlaying(isPlaying)
                        scope.launch {
                            while (isPlaying) {
                                progress(currentPosition)
                                delay(1_000L)
                            }
                        }
                        currentDuration(duration)
                        addListener(
                            object : Player.Listener {
                                override fun onPlaybackStateChanged(
                                    playbackState: Int,
                                ) {
                                    when (playbackState) {
                                        Player.STATE_IDLE -> {
                                            Timber.e("state IDLE...")
                                            progress(0L)
                                            isVoicePlaying(isPlaying)
                                            currentDuration(duration)
                                        }

                                        Player.STATE_ENDED -> {
                                            Timber.e("state ENDED...")
                                            isVoicePlaying(isPlaying)
                                            progress(0L)
                                            currentDuration(0L)
                                        }

                                        Player.STATE_BUFFERING -> {
                                            Timber.e("state BUFFERING...")
                                        }

                                        Player.STATE_READY -> {
                                            Timber.e("state READY...")
                                            isVoicePlaying(isPlaying)
                                            currentDuration(duration)
                                            scope.launch {
                                                while (isPlaying) {
                                                    progress(currentPosition)
                                                    Timber.e("position sett:$currentPosition")
                                                    delay(1_000L)
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
                                    Timber.e("is playing change: $isPlaying")
                                    isVoicePlaying(isPlaying)
                                    if (isPlaying.not())
                                        progress(0L)
                                    super.onIsPlayingChanged(isPlaying)
                                }


                                override fun onPlayerError(error: PlaybackException) {
                                    super.onPlayerError(error)
                                    Timber.e(error.message)
                                    browser?.stop()
                                }

                            })
                    }
                }, MoreExecutors.directExecutor())
            }
        onStopOrDispose {
            MediaBrowser.releaseFuture(browserFuture!!)
        }
    }
}