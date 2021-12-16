package com.experiment.voicerecorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.experiment.voicerecorder.ViewModel.PlayerViewModel
import com.experiment.voicerecorder.compose.PlaylistScaffold
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
class PlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: PlayerViewModel = viewModel()
            VoiceRecorderPermissionsHandler {
                PlaylistScaffold(
                    viewModel.allVoices,
                    onPlayPause = {},
                    onStop = {},
                    onVoiceClicked = { i, voice ->
                        viewModel.startPlaying()

                    }
                )
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