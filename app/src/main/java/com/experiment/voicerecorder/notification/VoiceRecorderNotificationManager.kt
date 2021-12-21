package com.experiment.voicerecorder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.experiment.voicerecorder.R
import com.experiment.voicerecorder.Utils.ACTION_PAUSE
import com.experiment.voicerecorder.Utils.ACTION_PLAY
import com.experiment.voicerecorder.Utils.ACTION_STOP
import com.experiment.voicerecorder.service.player.PlayPauseState
import com.experiment.voicerecorder.service.player.PlayerService
import timber.log.Timber

class VoiceRecorderNotificationManager(
    context: Context,
) {

    // TODO: 12/9/2021 make notification to work with the player and the recording
    enum class PlaybackState(
    ) {
        STATE_PLAY,
        STATE_PAUSE,
        STATE_STOP
    }

    fun showPlayerNotification(
        context: Context,
        mediaSession: MediaSessionCompat,
        title: String,
        subTitle: String,
        playPauseState: PlayPauseState,
//        pendingIntent: PendingIntent,
        autoCancel: Boolean = true,
    ) {
        NotificationManagerCompat.from(context).notify(
            PLAYBACK_ID,
            mediaStyleBuilder(
                context,
                mediaSession,
                playPauseState,
                title,
                subTitle,
//                pendingIntent,
                autoCancel).build())

        Timber.e("show player notification")
    }

    fun showRecorderNotification() {

    }

    fun removeNotification(context: Context, id: Int) {
        NotificationManagerCompat
            .from(context)
            .cancel(id)
    }

    fun showNotification(
        context: Context,
        channelId: String,
        smallIcon: Int,
        title: String,
        text: String,
        pendingIntent: PendingIntent,
        autoCancel: Boolean = true,
    ) {
        NotificationManagerCompat.from(context).notify(
            RECORDING_ID, notificationBuilder(
                context,
                channelId,
                smallIcon,
                title,
                text,
                pendingIntent,
                autoCancel
            ).build()
        )
    }

    private fun playerAction(
        context: Context,
        requestCode: Int,
    ): PendingIntent? {
        val intent = Intent(context, PlayerService::class.java)
        return when (requestCode) {
            0 -> {
                intent.action = ACTION_PLAY
                PendingIntent.getService(context, requestCode, intent, 0)
            }
            1 -> {
                intent.action = ACTION_PAUSE
                PendingIntent.getService(context, requestCode, intent, 0)
            }
            2 -> {
                intent.action = ACTION_STOP
                PendingIntent.getService(context, requestCode, intent, 0)
            }
            else -> {
                null
            }
        }
    }

    private fun mediaStyleBuilder(
        context: Context,
        mediaSession: MediaSessionCompat,
        playPauseState: PlayPauseState,
        title: String,
        text: String,
//        pendingIntent: PendingIntent,
        autoCancel: Boolean = true,
    ): NotificationCompat.Builder {
        val smallIcon = R.drawable.ic_play
        val largeIcon = BitmapFactory.decodeResource(context.resources,
            R.drawable.cover)
        var playPauseIcon = R.drawable.ic_pause
        var playPauseAction: PendingIntent? = null

        if (playPauseState == PlayPauseState.STATE_PLAY) {
            playPauseIcon = R.drawable.ic_pause
            playPauseAction = playerAction(context, 0)
        } else if (playPauseState == PlayPauseState.STATE_PAUSE) {
            playPauseIcon = R.drawable.ic_play
            playPauseAction = playerAction(context, 1)
        }
        // metadata is set in service using mediaSessions metadata method
        // getting data using preferences in player service
        return NotificationCompat.Builder(context, PLAYBACK_CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(autoCancel)
//            .setContentIntent(pendingIntent)
            .setStyle(MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0, 1))
            .setSmallIcon(smallIcon)
//            .setLargeIcon(largeIcon)
//            .setContentTitle(title)
//            .setContentText(text)
            .addAction(playPauseIcon, "play-pause", playPauseAction)
            .addAction(R.drawable.ic_stop, "stop", playerAction(context, 2))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    private fun notificationBuilder(
        context: Context,
        channelId: String,
        smallIcon: Int,
        title: String,
        text: String,
        pendingIntent: PendingIntent,
        autoCancel: Boolean = true,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(MediaStyle().setShowCancelButton(true))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(autoCancel)
    }

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val recorderChannelName = RECORDER_CHANNEL_NAME
            val playerChannelName = PLAYER_CHANNEL_NAME
            val recorderChannelId = RECORDING_CHANNEL_ID
            val playerChannelId = PLAYBACK_CHANNEL_ID
            val importance = NotificationManager.IMPORTANCE_HIGH
            val recorderNotificationChannel = NotificationChannel(recorderChannelId, recorderChannelName, importance)
            val playerNotificationChannel = NotificationChannel(playerChannelId, playerChannelName, importance)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(recorderNotificationChannel)
            notificationManager.createNotificationChannel(playerNotificationChannel)
        }
    }
}

// TODO: 12/12/2021 use resources on clean up
const val RECORDING_CHANNEL_ID = "RecordingChannel"
const val PLAYBACK_CHANNEL_ID = "PlayingChannel"
const val RECORDING_ID = 0
const val PLAYBACK_ID = 1
const val RECORDER_CHANNEL_NAME = "Recorder"
const val PLAYER_CHANNEL_NAME = "Player"
