package com.experiment.voicerecorder.service.player

import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.experiment.voicerecorder.Utils.PLAYER_ROOT_ID
import com.experiment.voicerecorder.notification.VoiceRecorderNotificationManager

//TODO: 12/15/2021 check music player implementation and see if this is the right way for our case

class VoicePlaybackService() : MediaBrowserServiceCompat() {

    lateinit var mediaPlayer: MediaPlayer
    lateinit var notification: VoiceRecorderNotificationManager
    lateinit var mediasession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        notification = VoiceRecorderNotificationManager(this)

    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot? {
        return BrowserRoot(PLAYER_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>,
    ) {
        if (parentId == PLAYER_ROOT_ID) {
            //send result
        }
        result.sendError(null)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    private fun initializePlayer() {
        MediaPlayer().apply {

        }
    }
}