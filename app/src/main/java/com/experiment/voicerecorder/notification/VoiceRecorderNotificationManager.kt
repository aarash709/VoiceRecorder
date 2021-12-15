package com.experiment.voicerecorder.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.experiment.voicerecorder.R

class VoiceRecorderNotificationManager(context: Context) {

    // TODO: 12/9/2021 make notification to work with the player and the recording

    val notificationManager =
        ContextCompat.getSystemService(context, NotificationManagerCompat::class.java)

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
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(autoCancel)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channelName = RECORDING_CHANNEL_NAME
            val channelId = RECORDING_CHANNEL_ID
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}

// TODO: 12/12/2021 use resources on clean up
const val RECORDING_CHANNEL_ID = "RecordingChannel"
const val RECORDING_ID = 0
const val PLAYBACK_ID = 1
const val RECORDING_CHANNEL_NAME = "Recording"
const val PLAYBACK_CHANNEL_ID = "PlayingChannel"