package com.experiment.voicerecorder.service.player

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.*
import android.media.session.MediaSessionManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.experiment.voicerecorder.R
import com.experiment.voicerecorder.Utils.*
import com.experiment.voicerecorder.notification.PLAYBACK_ID
import com.experiment.voicerecorder.notification.VoiceRecorderNotificationManager
import timber.log.Timber

const val PLAYER_SERVICE_TAG = "voicePlayer"

enum class PlayPauseState {
    STATE_PLAY,
    STATE_PAUSE
}

class PlayerService :
    Service(),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener,
    AudioManager.OnAudioFocusChangeListener {

    private var resumePosition: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private var mediaPath: String? = null
    private lateinit var audioManager: AudioManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionManager: MediaSessionManager
    private lateinit var notification: VoiceRecorderNotificationManager
    private lateinit var transportControls: MediaControllerCompat.TransportControls

    private val binder = LocalBinder()
    private val audioBecomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            pausePlaying()
            showNotification(PlayPauseState.STATE_PAUSE)
        }
    }
    private val playVoiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.e("Play voice receiver")

            stopPlaying()
            initializeMediaPlayer()
            showNotification(PlayPauseState.STATE_PLAY)
        }
    }
    private val pauseVoiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.e("Pause voice receiver")
            pausePlaying()
            //reset mediaPlayer?
            //build notification
            showNotification(PlayPauseState.STATE_PAUSE)
        }
    }
    private val stopVoiceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.e("Stop voice receiver")
            stopPlaying()
            //reset mediaPlayer?
            //build notification
            removeNotification()
        }
    }

    inner class LocalBinder() : Binder() {
        fun getService() = this@PlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder
    override fun onCreate() {
        super.onCreate()
        Timber.e("On create")
        registerBecomingNoisyReceiver()
        registerPlayVoiceReceiver()
        registerPauseVoiceReceiver()
        registerStopVoiceReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.e("start Command")
        mediaPlayer = MediaPlayer()
        notification = VoiceRecorderNotificationManager(
            this)
        getMediaFile(this)
        if (!requestAudioFocus())
            stopSelf()
        if (!mediaPath.isNullOrBlank()){
            initializeMediaSession()
            initializeMediaPlayer()
        }
        updateMetadata()
        showNotification(PlayPauseState.STATE_PLAY)
        handlePlaybackActions(intent)
        return START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mediaPath.isNullOrBlank()) {
            stopPlaying()
            mediaPlayer?.release()
        }
        mediaSession.release()
        mediaPlayer = null
        removeAudioFocus()
        removeNotification()
        StorageUtil(this).clearCach()
        unregisterReceiver(audioBecomingNoisyReceiver)
        unregisterReceiver(playVoiceReceiver)
        stopSelf()
    }
    private fun updateMetadata(){
        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.cover)
        mediaSession.setMetadata(MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE,"mediaPath")
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,"meta Subtitle")
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,"meta artist")
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,bitmap)
            .build())
    }
    private fun initializeMediaSession(){
        mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSession = MediaSessionCompat(this, PLAYER_SERVICE_TAG).apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS or
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
            isActive = true
            setCallback(mediaSessionCallback)
            transportControls = controller.transportControls
        }
    }
    private val mediaSessionCallback =
        object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                resumePlaying()
                showNotification(PlayPauseState.STATE_PLAY)
                Timber.e("onplay")
            }

            override fun onPause() {
                super.onPause()
                pausePlaying()
                showNotification(PlayPauseState.STATE_PAUSE)
                Timber.e("onpause")
            }

            override fun onStop() {
                super.onStop()
                Timber.e("onstop")
                stopPlaying()
                removeNotification()
                stopSelf()
            }
        }
    private fun handlePlaybackActions(intent: Intent?){
        when(intent?.action){
            ACTION_PLAY->
                transportControls.play()
            ACTION_PAUSE->
                transportControls.pause()
            ACTION_STOP->
                transportControls.stop()
        }
    }
    private fun initializeMediaPlayer() {
        mediaPlayer?.apply {
            setOnPreparedListener(this@PlayerService)
            setOnErrorListener(this@PlayerService)
            setOnCompletionListener(this@PlayerService)
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

    private fun showNotification(playState: PlayPauseState) {
        notification.showPlayerNotification(
            this,
            mediaSession,
            "Title",
            "subtitle",
            playState,
        )
    }
    private fun removeNotification(){
        notification.removeNotification(this, PLAYBACK_ID)
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

    private fun registerBecomingNoisyReceiver() {
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(audioBecomingNoisyReceiver, intentFilter)
    }

    private fun registerPlayVoiceReceiver() {
        val intentFilter = IntentFilter(BROADCAST_PLAY_VOICE)
        registerReceiver(playVoiceReceiver, intentFilter)
    }
    private fun registerPauseVoiceReceiver() {
        val intentFilter = IntentFilter(BROADCAST_PAUSE_VOICE)
        registerReceiver(pauseVoiceReceiver, intentFilter)
    }
    private fun registerStopVoiceReceiver() {
        val intentFilter = IntentFilter(BROADCAST_STOP_VOICE)
        registerReceiver(stopVoiceReceiver, intentFilter)
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
                resumePosition = currentPosition
            }
        }
    }

    fun resumePlaying() {
        mediaPlayer?.apply {
            if (isPlaying) {
                seekTo(resumePosition)
                start()
            }
        }
    }

    //player Listeners
    override fun onPrepared(mp: MediaPlayer?) {
        mp?.let {
            Timber.e("On Prepared")
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
            Timber.e("onCompletion")
            stopPlaying()
            removeNotification()
            stopSelf()
        }
    }

}
