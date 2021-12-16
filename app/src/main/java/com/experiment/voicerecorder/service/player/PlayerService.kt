package com.experiment.voicerecorder.service.player

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
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
    MediaPlayer.OnCompletionListener,
    AudioManager.OnAudioFocusChangeListener
{

    private var mediaPlayer: MediaPlayer? = null
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

    override fun onAudioFocusChange(focusChange: Int) {
       when(focusChange){
           AudioManager.AUDIOFOCUS_GAIN->{
               if (mediaPlayer== null) mediaPlayer = MediaPlayer()
               mediaPlayer?.apply {
                   if (!isPlaying){
                       startPlaying()
                   }
               }
           }
           AudioManager.AUDIOFOCUS_LOSS->{
               mediaPlayer?.apply {
                   if (isPlaying){
                       stopPlaying()
                       release()
                   }
                   mediaPlayer = null
               }
           }
           AudioManager.AUDIOFOCUS_LOSS_TRANSIENT->{
               mediaPlayer?.apply {
                   if (isPlaying){
                       pause()
                   }
               }
           }
           AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK->{
               mediaPlayer?.apply {
                   if (isPlaying){
                       setVolume(0.1f, 0.1f)
                   }
               }
           }
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
        when(what){
            MediaPlayer.MEDIA_ERROR_IO->{
                Timber.e("Media player IO Error.$extra")
            }
            MediaPlayer.MEDIA_ERROR_MALFORMED->{
                Timber.e("Media Error Malformed.$extra")
            }
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED->{
                Timber.e("Media player unsupported Error.$extra")
            }
            MediaPlayer.MEDIA_ERROR_UNKNOWN->{
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
