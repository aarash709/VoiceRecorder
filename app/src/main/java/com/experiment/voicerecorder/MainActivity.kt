package com.experiment.voicerecorder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.compose.rememberNavController
import com.experiment.voicerecorder.ui.MainScreen
import com.experiment.voicerecorder.ui.VoiceRecorderNavigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import com.recorder.service.PlayerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var playerService: PlayerService
    private var isPlayerServiceBound = MutableStateFlow(false)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Timber.e("service connection")
            val binder = service as PlayerService.LocalBinder
            playerService = binder.getService()
            isPlayerServiceBound.update { true }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isPlayerServiceBound.update { false }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(this, PlayerService::class.java).also {
            bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        setContent {
            VoiceRecorderTheme {
                val navState = rememberNavController()
                val isPlaying =
                    if (isPlayerServiceBound.collectAsState().value)
                        playerService.isPlaying.collectAsState().value
                    else false
                val voices = if (isPlayerServiceBound.collectAsState().value)
                    playerService.voices.collectAsState().value
                else
                    listOf()

                MainScreen {
                    Box(modifier = Modifier) {
                        VoiceRecorderNavigation(
                            modifier = Modifier,
                            navController = navState,
                            voices = voices,
                            isPlaying = isPlaying,
                            onPlay = { index, voice ->
                                if (isPlayerServiceBound.value) {
                                    playerService.onPlay(voice, index)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this,
            ComponentName(this, PlayerService::class.java)
        )
        val mediaController = MediaController.Builder(this,sessionToken).buildAsync()
        mediaController.addListener(
            { mediaController.get() },
            ContextCompat.getMainExecutor(this)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {

    }
}
