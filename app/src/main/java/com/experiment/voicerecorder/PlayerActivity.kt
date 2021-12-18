package com.experiment.voicerecorder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.experiment.voicerecorder.Utils.FILE_PATH
import com.experiment.voicerecorder.Utils.SERVICE_STATE
import com.experiment.voicerecorder.ViewModel.PlayerViewModel
import com.experiment.voicerecorder.compose.PlaylistScaffold
import com.experiment.voicerecorder.service.player.PlayerService
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
class PlayerActivity : ComponentActivity() {

    private var isServiceBound = false
    private lateinit var playerService: PlayerService
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.LocalBinder
            playerService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {

            isServiceBound = false
        }
    }
    fun playVoice(voice: String){
        //start service
        if (!isServiceBound){
            val playerServiceIntent = Intent(this,PlayerService::class.java).apply {
                putExtra(FILE_PATH,voice)
            }
            startService(playerServiceIntent)
            bindService(playerServiceIntent,serviceConnection,Context.BIND_AUTO_CREATE)
        }else {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: PlayerViewModel = viewModel()
            val lifecycleOwner = LocalLifecycleOwner.current
            val voices = viewModel.allVoices

            DisposableEffect(key1 = lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_CREATE) {
                        if (voices.isEmpty())
                            viewModel.getAllVoices()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    voices.clear()
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            MaterialTheme {
                VoiceRecorderPermissionsHandler {
                    PlaylistScaffold(
                        voices,
                        onPlayPause = {},
                        onStop = {},
                        onVoiceClicked = { i, voice ->
                            playVoice(
                                voice.path)

                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound){
            unbindService(serviceConnection)
            playerService.stopSelf()
        }
    }
//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putBoolean(SERVICE_STATE,isServiceBound)
//        super.onSaveInstanceState(outState)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        savedInstanceState.putBoolean(SERVICE_STATE,isServiceBound)
//    }

}

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
fun startPlayerActivity(context: Context) {
    val intent = Intent(context, PlayerActivity::class.java)
    context.startActivity(intent)
}
