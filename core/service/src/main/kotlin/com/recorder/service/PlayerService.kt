package com.recorder.service

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PlayerService : MediaLibraryService() {

    private lateinit var mediaLibrarySession: MediaLibrarySession

    private lateinit var exoPlayer: ExoPlayer

    private val callback = MediaLibraryCallback()

    inner class MediaLibraryCallback() : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            return super.onConnect(session, controller)
        }

        override fun onDisconnected(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ) {
            super.onDisconnected(session, controller)
            if (session.player.isPlaying.not()){
                session.player.release()
            }
        }


        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return super.onGetItem(session, browser, mediaId)
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
        ): ListenableFuture<MutableList<MediaItem>> {
            mediaItems.forEach {
                Timber.e(it.mediaId)
            }
            return Futures.immediateFuture(mediaItems)
        }
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        mediaLibrarySession = MediaLibrarySession.Builder(
            this,
            exoPlayer,
            callback,
        )
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaLibrarySession.run {
            player.release()
            release()
        }
    }
}