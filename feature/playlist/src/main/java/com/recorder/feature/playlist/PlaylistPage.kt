package com.recorder.feature.playlist

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import timber.log.Timber

@ExperimentalMaterialApi
@Composable
fun Playlist(
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    voices: List<Voice>,
    isPlaying: Boolean,
    onVoiceClicked: (Int, Voice) -> Unit,
    onBackPressed: () -> Unit = {},
) {
    val viewModel = hiltViewModel<PlaylistViewModel>()
    val context = LocalContext.current
    var playingVoiceIndex by remember {
        mutableStateOf(0)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colors.background)
    ) {
        PlaylistContent(
            voices = voices,
            isPlaying = isPlaying,
            onPlayPause = { },
            onStop = { },
            onVoiceClicked = { voiceIndex, voice ->
                playingVoiceIndex = voiceIndex
                onVoiceClicked(voiceIndex, voice)
            },
            onBackPressed = { onBackPressed() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistContent(
    voices: List<Voice>,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onVoiceClicked: (Int, Voice) -> Unit,
    onBackPressed: () -> Unit,
) {
    var voice by remember {
        mutableStateOf(Voice())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 0.dp)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Recordings",
                    color = MaterialTheme.colors.onBackground
                )
            },
            navigationIcon = {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "back icon"
                    )
                }
            },
            colors = TopAppBarDefaults
                .centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colors.background
                )
        )
        LazyColumn(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                count = voices.size,
                key = {
                    it
                }) { voiceIndex ->
                PlaylistItem(
                    modifier = Modifier,
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
        onClick = {
            onVoiceClicked(Voice(voice.title, voice.path))
            Timber.e("ui item: ${voice.title}") },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colors.surface,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (voice.isPlaying)
                Icon(
                    imageVector = Icons.Default.StopCircle,
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(all = 8.dp),
                    contentDescription = ""
                )
            else
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(all = 8.dp),
                    contentDescription = ""
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
fun PlaylistPagePreview() {
    VoiceRecorderTheme {
        Surface() {
            PlaylistContent(
                listOf(
                    Voice("title", "", isPlaying = false, "00:01"),
                    Voice("title2", "", isPlaying = true, "00:10"),
                    Voice("title3", "", isPlaying = false, "02:21"),
                    Voice("title4", "", isPlaying = false, "05:01"),
                    Voice("title5", "", isPlaying = false, "00:41")
                ),
                isPlaying = false,
                onPlayPause = {},
                onStop = {},
                onVoiceClicked = { i, voice ->
                },
                onBackPressed = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PlaylistItemPreview() {
    VoiceRecorderTheme {
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
    VoiceRecorderTheme {
        MediaControls(
            modifier = Modifier,
            voice = Voice(),
            onPlayPause = {},
            onStop = {}
        )
    }
}