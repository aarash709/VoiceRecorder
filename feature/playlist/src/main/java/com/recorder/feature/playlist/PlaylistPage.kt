package com.recorder.feature.playlist

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalMaterialApi
@Composable
fun Playlist(
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onVoiceClicked: (Int, Voice) -> Unit,
) {

    val viewModel = hiltViewModel<PlaylistViewModel>()
    val voices = viewModel.voices.collectAsStateWithLifecycle().value

    val bottomSheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    var index by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(key1 = voices) {
        if (!voices[index].isPlaying)
            bottomSheetState.bottomSheetState.collapse()
    }
    LaunchedEffect(key1 = true) {
        //load voices once the page is composed
    }
    BottomSheetScaffold(
        sheetContent =
        {
            MediaControls(
                voice = (voices[index]),
                onPlayPause = { voice->
                    viewModel.onPlay(index, voice) },
                onStop = {
                    viewModel.onStop()
                    scope.launch {
                        bottomSheetState.bottomSheetState.collapse()
                    }
                }
            )
        },
        scaffoldState = bottomSheetState,
        sheetShape = RoundedCornerShape(8.dp),
        sheetPeekHeight = 0.dp,
        backgroundColor = MaterialTheme.colors.background
    ) {
        PlaylistContent(
            voices = voices,
            isPlaying = false,
            onPlayPause = { },
            onStop = { },
            onVoiceClicked = { i, voice ->
                index = i
                viewModel.onPlay(0,voice)
                scope.launch {
                    bottomSheetState.bottomSheetState.expand()
                }
            })
    }
}

@Composable
fun PlaylistContent(
    voices: List<Voice>,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onVoiceClicked: (Int, Voice) -> Unit,
) {
    var voice by remember {
        mutableStateOf(Voice())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 0.dp)
    ) {
        LazyColumn {
//            itemsIndexed(
//                items = voices,
//            ) { index, voice ->
//                PlaylistItem(
//                    voice = voice,
////                isPlaying = isPlaying,
//                    onVoiceClicked = { voice ->
//                        onVoiceClicked(index, voice)
//                    })
//            }
            items(
                count = voices.size,
                key = {
                    it
                }) { voiceIndex ->
                PlaylistItem(
                    voice = voices[voiceIndex]
                ) { clickedVoice ->
                    onVoiceClicked(voiceIndex, clickedVoice)
                    voice = clickedVoice
                }
            }
        }

    }
}

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    voice: Voice,
    onVoiceClicked: (Voice) -> Unit,
) {
    val textColor = if (voice.isPlaying) MaterialTheme.colors.primary
     else MaterialTheme.colors.onSurface
    Surface(
        modifier = Modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colors.surface,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable() {
                    onVoiceClicked(Voice(voice.title, voice.path, voice.isPlaying))
                    Timber.e(voice.title)
                    Timber.e("is playing voice: ${voice.isPlaying}")
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (voice.isPlaying)
                Icon(
                    imageVector = Icons.Default.StopCircle,
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(all = 8.dp)
                )
            else
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(all = 8.dp)
                )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = voice.title,
                    color = textColor
                )
                Row {
                    Text(
                        text = voice.duration,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = voice.recordTime,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

    }
}

@Composable
fun MediaControls(
    modifier: Modifier = Modifier,
    voice: Voice,
    onPlayPause: (Voice) -> Unit,
    onStop: () -> Unit,
) {
    var sliderInt by remember {
        mutableStateOf(0f)
    }
    Card(modifier = modifier) {
        Column() {
            Text(text = "filename:${voice.title}")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { onPlayPause(voice) }) {
                    Timber.e("${voice.isPlaying}")
                    if (voice.isPlaying)
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Play/Pause Button"
                        )
                    else
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause Button"
                        )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onStop() }) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Button"
                    )
                }
            }
            Slider(value = sliderInt, onValueChange = { sliderInt = it })
        }

    }
}
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun PlaylistPagePreview(){
    VoiceRecorderTheme {
        Surface() {
            PlaylistContent(
                listOf(
                    Voice("title", "", isPlaying = false, "00:01",),
                    Voice("title2", "", isPlaying = true, "00:10",),
                    Voice("title3", "", isPlaying = false, "02:21",),
                    Voice("title4", "", isPlaying = false, "05:01",),
                    Voice("title5", "", isPlaying = false, "00:41",)
                ),
                isPlaying = false,
                onPlayPause = {},
                onStop = {},
                onVoiceClicked = { i, voice ->
                }
            )
        }
    }
}
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PlaylistItemPreview() {
    VoiceRecorderTheme{
        Surface {
            PlaylistItem(
                voice = Voice(
                    title = "title prview",
                    path = "path",
                    isPlaying = false,
                    duration = "00:12",
                    recordTime = "just now"
                ), onVoiceClicked = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MediaControlsPreview() {
    VoiceRecorderTheme{
        MediaControls(
            modifier = Modifier,
            voice = Voice(),
            onPlayPause = {},
            onStop = {}
        )
    }
}