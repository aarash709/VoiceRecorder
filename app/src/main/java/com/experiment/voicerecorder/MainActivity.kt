package com.experiment.voicerecorder

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.experiment.voicerecorder.compose.MainScreen
import com.experiment.voicerecorder.compose.VoiceRecorderNavigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import com.recorder.service.PlayerService
import dagger.hilt.android.AndroidEntryPoint

//@RequiresApi(Build.VERSION_CODES.R)
@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var playerService : PlayerService
    private var isPlayerServiceBound = false

    private val _isPlaying = playerService.isPlaying.value

    private val serviceConnectin = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.LocalBinder
            playerService = service.getBinder()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoiceRecorderTheme {
                val navState = rememberNavController()
                //end ui state
                val lifecycleOwner = LocalLifecycleOwner.current
                val textSize by remember {
                    mutableStateOf(12.sp)
                }
                DisposableEffect(key1 = lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_CREATE) {
//                            viewModel.getAllVoices()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                MainScreen {
                    Box(modifier = Modifier) {
                        VoiceRecorderNavigation(
                            Modifier,
                            navState,
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this,PlayerService::class.java).also {
            bindService(it,serviceConnectin, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnectin)
    }

    @Composable
    private fun Debug(
        playButtonState: Boolean,
        recordButtonState: Boolean,
        fileName: String,
        state: String,
        textSize: TextUnit,
        pos: Int,
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Bottom) {
            Greeting(name = "Arash")
            if (fileName.isEmpty())
                Text(text = "file name is: Press Record Button to appear",
                    color = MaterialTheme.colors.onSurface,
                    fontSize = textSize
                )
            else Text(text = "file name is: ${fileName}",
                color = MaterialTheme.colors.onSurface,
                fontSize = textSize
            )
            Text(text = "state: ${state}",
                color = MaterialTheme.colors.onSurface,
                fontSize = textSize
            )
            Text(text = "pos: ${pos}",
                color = MaterialTheme.colors.onSurface,
                fontSize = textSize
            )
        }
    }
}


@Composable
fun PlayAudioButton(
    modifier: Modifier = Modifier,
    startPlaying: () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start) {
        OutlinedButton(onClick = {
            startPlaying()
        },
            modifier = Modifier.size(100.dp),
            shape = CircleShape) {
            Image(painter = rememberImagePainter(
                data = R.drawable.ic_play),
                contentDescription = "Play Button Icon")
        }

    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!",
        color = MaterialTheme.colors.onSurface)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VoiceRecorderTheme {
        Greeting("Android")
    }


}