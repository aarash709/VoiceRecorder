package com.experiment.voicerecorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.experiment.voicerecorder.ViewModel.PlayerViewModel
import com.experiment.voicerecorder.ViewModel.VoiceRecorderState
import com.experiment.voicerecorder.compose.PlaylistScaffold
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
class PlayerActivity : ComponentActivity() {

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
                            viewModel.startPlaying()

                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}

@ExperimentalMaterialApi
@ExperimentalPermissionsApi
fun startPlayerActivity(context: Context) {
    val intent = Intent(context, PlayerActivity::class.java)
    context.startActivity(intent)
}