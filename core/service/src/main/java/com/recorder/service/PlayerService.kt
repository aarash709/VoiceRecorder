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
            Timber.e("onconnect")
            return super.onConnect(session, controller)
        }


        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String,
        ): ListenableFuture<LibraryResult<MediaItem>> {
            Timber.e("on get item")
            return super.onGetItem(session, browser, mediaId)
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
        ): ListenableFuture<MutableList<MediaItem>> {
            Timber.e("onaddmediaitem")
           mediaItems.forEach{
               Timber.e(it.mediaId)
           }
            return Futures.immediateFuture(mediaItems)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.e("player service created")

        exoPlayer = ExoPlayer.Builder(this).build()
        mediaLibrarySession = MediaLibrarySession.Builder(
            this,
            exoPlayer,
            callback,
        )
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        Timber.e("getsession")
        return mediaLibrarySession
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("player service destroyed")
    }

}