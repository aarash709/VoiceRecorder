package com.experiment.voicerecorder.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import timber.log.Timber

@Composable
fun rememberPlayerState(
    browser: MediaBrowser?,
): PlayerState {
    val lifecycleOwner = LocalLifecycleOwner.current
    PlayerStateEffect(browser = browser, lifecycleOwner = lifecycleOwner)
    return remember(key1 = browser) {
        PlayerState(browser = browser)
    }
}

@Stable
class PlayerState(private val browser: MediaBrowser?) : Player.Listener {
    var isVoicePlaying = browser?.isPlaying ?: false
    var progress = browser?.currentPosition ?: 0L
    var voiceDuration = 0L

    init {
        Timber.e("init state")
        browser?.addListener(this)
    }

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
                isVoicePlaying = browser?.isPlaying ?: false
                voiceDuration = 0
            }

            Player.STATE_ENDED -> {
                isVoicePlaying = browser?.isPlaying ?: false
                progress = 0
                voiceDuration = 0
                Timber.e("ended")
            }

            Player.STATE_BUFFERING -> {

            }

            Player.STATE_READY -> {
                voiceDuration = browser?.duration ?: 0L
                progress = browser?.currentPosition ?: 0L
                Timber.e("ready")
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
        isVoicePlaying = browser?.isPlaying ?: false
        super.onPlayWhenReadyChanged(
            playWhenReady,
            reason
        )
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        isVoicePlaying = isPlaying
        super.onIsPlayingChanged(isPlaying)
        Timber.e("is playing chnage:$isPlaying")
    }


    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Timber.e(error.message)
        browser?.stop()
    }

}

@Composable
fun PlayerStateEffect(browser: MediaBrowser?, lifecycleOwner: LifecycleOwner) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event.targetState == Lifecycle.State.STARTED) {
                browser?.run {

                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}