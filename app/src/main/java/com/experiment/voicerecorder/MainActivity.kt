package com.experiment.voicerecorder

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.experiment.voicerecorder.ViewModel.MainViewModel
import com.experiment.voicerecorder.ViewModel.VoiceRecorderState
import com.experiment.voicerecorder.compose.PlaylistScaffold
import com.experiment.voicerecorder.compose.RecodingPage
import com.experiment.voicerecorder.compose.VoiceRecorderNavigation
import com.experiment.voicerecorder.ui.theme.VoiceRecorderTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.R)
@ExperimentalPermissionsApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VoiceRecorderTheme {
                val viewModel: MainViewModel = viewModel()
                val voiceRecorderState = viewModel.appState.value
                val isRecording = viewModel.isRecording.value
                val fileName = viewModel.fileName.value
                val duration = viewModel.duration.value
                val isPlaying = viewModel.isPlaying.value
                val timer = viewModel.timer.value
                val seekbarPosition = viewModel.seekbarCurrentPosition.value
                val maxPlaybackTime = viewModel.voiceDuration.value

                //ui states

                var playButtonState by remember {
                    mutableStateOf(false)
                }
                var recordButtonState by remember {
                    mutableStateOf(false)
                }
                var playlistButtonEnabled by remember {
                    mutableStateOf(false)
                }
                var voiceIndex by remember {
                    mutableStateOf(0)
                }
                val navState = rememberNavController()
                //end ui state

                val lifecycleOwner = LocalLifecycleOwner.current
                val textSize by remember {
                    mutableStateOf(12.sp)
                }
                LaunchedEffect(key1 = voiceRecorderState, key2 = seekbarPosition, key3 = timer) {

                    when (voiceRecorderState) {
                        VoiceRecorderState.STATE_RECORDING -> {
                            playButtonState = false
                            playlistButtonEnabled = false
                            viewModel.updateTimerValues()
                        }
                        VoiceRecorderState.STATE_NOT_RECORDING -> {
                            playButtonState = true
                            recordButtonState = true
                            playlistButtonEnabled = true
                            viewModel.resetRecordingTimer()

                        }
                        VoiceRecorderState.STATE_PLAYING -> {
                            recordButtonState = false
                            Timber.e("voice index $voiceIndex")
//                            viewModel.onPlayUpdateListState(voiceIndex)
                        }
                        VoiceRecorderState.STATE_NOT_PLAYING -> {
                            recordButtonState = true
                            playButtonState = true
                        }
                        VoiceRecorderState.STATE_IDLE -> {
                            recordButtonState = true
                            playButtonState = true
                            playlistButtonEnabled = true
                            viewModel.onPlayUpdateListState(voiceIndex)
                            viewModel.resetRecordingTimer()
                            viewModel.resetPlayerValues()
                        }
                        else -> {}
                    }
                }
                DisposableEffect(key1 = lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_CREATE) {
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                MainScreen {
                    Box(modifier = Modifier) {
                        Debug(
                            playButtonState,
                            recordButtonState,
                            fileName,
                            voiceRecorderState,
                            textSize,
                            seekbarPosition)
                        RecodingPage(modifier = Modifier,
                            recordEnabled = recordButtonState,
                            recordingTime = timer,
                            playlistButtonEnabled,
                            onRecord = { viewModel.onRecord() },
                            onListButtonClick = { startPlayerActivity(this@MainActivity) })
                    }
                }
            }
        }
    }
}

@Composable
private fun Debug(
    playButtonState: Boolean,
    recordButtonState: Boolean,
    fileName: String,
    state: VoiceRecorderState,
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