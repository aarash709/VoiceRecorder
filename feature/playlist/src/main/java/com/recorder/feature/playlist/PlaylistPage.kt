package com.recorder.feature.playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core.common.model.Voice
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalMaterialApi
@Composable
fun PlaylistScaffold(
    voices: List<Voice>,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onVoiceClicked: (Int, Voice) -> Unit,
) {
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
                onPlayPause = onPlayPause,
                onStop = {
                    onStop()
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
        Playlist(
            voices = voices,
            isPlaying = false,
            onPlayPause = onPlayPause,
            onStop = onStop,
            onVoiceClicked = { i, voice ->
                index = i
                onVoiceClicked(i, voice)
                scope.launch {
                    bottomSheetState.bottomSheetState.expand()
                }
            })
    }
}

@Composable
fun Playlist(
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
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom) {
        MediaControls(Modifier, voice, onPlayPause = { onPlayPause() }, onStop = { onStop() })
    }
}

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    voice: Voice,
    onVoiceClicked: (Voice) -> Unit,
) {
    val textColor = if (voice.isPlaying) Color.Cyan else MaterialTheme.colors.onSurface
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable() {
                onVoiceClicked(Voice(voice.title, voice.path, voice.isPlaying))
                Timber.e(voice.title)
                Timber.e("is playing voice: ${voice.isPlaying}")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (voice.isPlaying)
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "",
                modifier = Modifier
                    .size(75.dp)
                    .padding(all = 8.dp)
                    .clip(CircleShape)
            )
        else
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "",
                modifier = Modifier
                    .size(75.dp)
                    .padding(all = 8.dp)
                    .clip(CircleShape)
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

@Composable
fun MediaControls(
    modifier: Modifier = Modifier,
    voice: Voice,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
) {
    var sliderInt by remember {
        mutableStateOf(0f)
    }
    Card(modifier = modifier) {
        Column() {
            Text(text = "filename:${voice.title}")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = { onPlayPause() }) {
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

@Composable
@Preview(showBackground = true)
fun MediaControlsPreview() {
    MediaControls(
        modifier = Modifier,
        voice = Voice(),
        onPlayPause = {},
        onStop = {}
    )
    PlaylistItem(
        voice = Voice(
            title = "titljjjjjjjjjjjjjjjjjjjje",
            path = "path",
            isPlaying = false,
            duration = "00:00",
            recordTime = "just now"
        )
    ) {
//        PlaylistPage(voices = vm.allVoices) {
//
//        }
    }
}