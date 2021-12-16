package com.experiment.voicerecorder.service.player

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import timber.log.Timber
import java.lang.Exception
import java.lang.NullPointerException

const val ACTION_PLAY = "com.experiment.voicerecorder.action.play"

class PlayerService :
    Service(),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {
    var mediaPlayer: MediaPlayer? = null
    private var mediaPath: String? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer()
        getMediaFile(intent)
        initMediaPlayer()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        stopSelf()
    }

    private fun initMediaPlayer() {
        mediaPlayer?.apply {
            setOnPreparedListener(this@PlayerService)
            setOnErrorListener(this@PlayerService)
            reset()
            setAudioAttributes(AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build())
            try {
                setDataSource(mediaPath)
            } catch (e: Exception) {
                e.printStackTrace()
                stopSelf()
            }
            prepareAsync()
        }
    }

    private fun getMediaFile(intent: Intent?) {
        try {
            mediaPath = intent?.getStringExtra("FILE_PATH")
        } catch (e: NullPointerException) {
            Timber.e("Media path is null")
            stopSelf()
        }
    }

    //player methods
    fun startPlaying() {
        mediaPlayer?.apply {
            if (!isPlaying) {
                start()
            }
        }
    }

    fun stopPlaying() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
        }
    }
    fun pausePlaying(){
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                // TODO: 12/16/2021 save resume position
            }
        }
    }
    fun resumePlaying(){
        mediaPlayer?.apply {
            if (isPlaying) {
                // TODO: 12/16/2021 get resume position
                start()
            }
        }
    }

    //player Listeners
    override fun onPrepared(mp: MediaPlayer?) {
        mp?.let {
            startPlaying()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mp?.let {
            stopPlaying()
            stopSelf()
        }
    }
}
