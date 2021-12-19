package com.experiment.voicerecorder.service.player

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.experiment.voicerecorder.Utils.BROADCAST_PLAY_VOICE
import com.experiment.voicerecorder.Utils.FILE_PATH
import com.experiment.voicerecorder.Utils.StorageUtil
import timber.log.Timber
import java.lang.Exception
import java.lang.NullPointerException

class PlayerService :
    Service(),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener,
    AudioManager.OnAudioFocusChangeListener {

    private var mediaPlayer: MediaPlayer? = null
    private var mediaPath: String? = null
    private lateinit var audioManager: AudioManager
    private val binder = LocalBinder()
    private val audioBecomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            pausePlaying()
            //build notification
        }
    }
    private val playVoiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mediaPath = StorageUtil(this@PlayerService).loadVoice()
            if (mediaPath.isNullOrBlank())
                stopSelf()
            stopPlaying()
            initMediaPlayer()
            //reset mediaPlayer?
            //build notification
        }
    }

    inner class LocalBinder() : Binder() {
        fun getService() = this@PlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder
    override fun onCreate() {
        super.onCreate()
        registerBecomingNoisyReceiver()
        registerPlayVoiceReceiver()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer()
        getMediaFile(this)
        if (!requestAudioFocus())
            stopSelf()
        if (!mediaPath.isNullOrBlank())
            initMediaPlayer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mediaPath.isNullOrBlank()) {
            stopPlaying()
            mediaPlayer?.release()
        }
        mediaPlayer = null
        removeAudioFocus()
        //remove Notification
        StorageUtil(this).clearCach()
        unregisterReceiver(audioBecomingNoisyReceiver)
        unregisterReceiver(playVoiceReceiver)
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
            Timber.e("media path: $mediaPath")
            prepareAsync()
        }
    }

    private fun getMediaFile(context: Context) {
        try {
            mediaPath = StorageUtil(context).loadVoice()
        } catch (e: NullPointerException) {
            Timber.e("Media path is null")
            stopSelf()
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mediaPlayer == null) mediaPlayer = MediaPlayer()
                mediaPlayer?.apply {
                    if (!isPlaying) {
                        startPlaying()
                    }
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer?.apply {
                    if (isPlaying) {
                        stopPlaying()
                        release()
                    }
                    mediaPlayer = null
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer?.apply {
                    if (isPlaying) {
                        pause()
                    }
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer?.apply {
                    if (isPlaying) {
                        setVolume(0.1f, 0.1f)
                    }
                }
            }
        }
    }
    private fun registerBecomingNoisyReceiver(){
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(audioBecomingNoisyReceiver,intentFilter)
    }
    private fun registerPlayVoiceReceiver(){
        val intentFilter = IntentFilter(BROADCAST_PLAY_VOICE)
        registerReceiver(playVoiceReceiver,intentFilter)
    }
    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(AudioManager::class.java) as AudioManager
        val result = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            audioManager.requestAudioFocus(AudioFocusRequest
                .Builder(AudioManager
                    .AUDIOFOCUS_GAIN)
                .setAudioAttributes(AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build())
                .build())
        else
            audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            return true
        return false
    }

    private fun removeAudioFocus() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            audioManager.abandonAudioFocusRequest(AudioFocusRequest
                .Builder(AudioManager
                    .AUDIOFOCUS_GAIN)
                .setAudioAttributes(AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build())
                .build())
        else
            audioManager.abandonAudioFocus(this)
    }

    //player methods
    private fun startPlaying() {
        mediaPlayer?.apply {
            if (!isPlaying) {
                start()
            }
        }
    }

    private fun stopPlaying() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
        }
    }

    fun pausePlaying() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
                // TODO: 12/16/2021 save resume position
            }
        }
    }

    fun resumePlaying() {
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
        when (what) {
            MediaPlayer.MEDIA_ERROR_IO -> {
                Timber.e("Media player IO Error.$extra")
            }
            MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                Timber.e("Media Error Malformed.$extra")
            }
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> {
                Timber.e("Media player unsupported Error.$extra")
            }
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                Timber.e("Media player IO Error.$extra")
            }
        }
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mp?.let {
            stopPlaying()
            stopSelf()
        }
    }

}
