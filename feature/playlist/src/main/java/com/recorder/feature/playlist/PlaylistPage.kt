package com.recorder.feature.playlist

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import timber.log.Timber

@Composable
fun Playlist(
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    voices: List<Voice>,
    progress: Float,
    duration: Float,
    onProgressChange: (Float) -> Unit,
    isPlaying: Boolean,
    onVoiceClicked: (Int, Voice) -> Unit,
    onBackPressed: () -> Unit,
) {
    val viewModel = hiltViewModel<PlaylistViewModel>()
    val context = LocalContext.current
    var playingVoiceIndex by remember {
        mutableIntStateOf(-1)
    }
    val voiceList by viewModel.voices.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit, block = {
        viewModel.getVoices(context)
    })
    LaunchedEffect(key1 = isPlaying) {
        Timber.e("updatelist")
        viewModel.updateVoiceList(
            selectedVoiceIndex = playingVoiceIndex,
            isPlaying = isPlaying
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PlaylistContent(
            voices = voiceList,
            onPlayPause = { },
            onStop = { onStop() },
            onVoiceClicked = { voiceIndex, voice ->
                playingVoiceIndex = voiceIndex
                onVoiceClicked(voiceIndex, voice)
            },
            onBackPressed = { onBackPressed() },
            progress = progress,
            duration = duration,
            onProgressChange = { progress ->
                onProgressChange(progress)
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistContent(
    voices: List<Voice>,
    progress: Float,
    duration: Float,
    onProgressChange: (Float) -> Unit,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onVoiceClicked: (Int, Voice) -> Unit,
    onBackPressed: () -> Unit,
) {
    var voice by remember {
        mutableStateOf(Voice())
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 0.dp)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        MediumTopAppBar(
            title = {
                Text(
                    text = "Recordings",
                )
            },
            navigationIcon = {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "back icon"
                    )
                }
            },
            colors = TopAppBarDefaults
                .mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
            scrollBehavior = scrollBehavior
        )
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = voices.size,
                key = {
                    it
                }) { voiceIndex ->
                PlaylistItem(
                    modifier = Modifier,
                    voice = voices[voiceIndex],
                    onVoiceClicked = { clickedVoice ->
                        onVoiceClicked(voiceIndex, clickedVoice)
                        voice = clickedVoice
                    },
                    onStop = { onStop() },
                    progress = progress,
                    duration = duration,
                    onProgressChange = { progress ->
                        onProgressChange(progress)
                    }
                )
            }
        }

    }
}

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    voice: Voice,
    progress: Float,
    duration: Float,
    onProgressChange: (Float) -> Unit,
    onVoiceClicked: (Voice) -> Unit,
    onStop: () -> Unit,
) {
    Surface(
        modifier = Modifier,
        onClick = { },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        val textColor = if (voice.isPlaying) MaterialTheme.colorScheme.primary
        else LocalContentColor.current
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(
                targetState = voice.isPlaying,
                label = "play icon") { isPlaying->
                if (isPlaying)
                    Icon(
                        imageVector = Icons.Default.StopCircle,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(all = 8.dp)
                            .clip(CircleShape)
                            .clickable { onStop() },
                        contentDescription = ""
                    )
                else
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(all = 8.dp)
                            .clip(CircleShape)
                            .clickable {
                                onVoiceClicked(Voice(voice.title, voice.path))
                                Timber.e("ui item: ${voice.title}")
                            },
                        contentDescription = ""
                    )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = voice.title,
                    color = textColor
                )
                if(voice.isPlaying) {
                    Slider(
                        value = progress,
                        onValueChange = { onProgressChange(it) },
                        modifier = Modifier,
                        valueRange = 0f..duration,
                        steps = 0,
                        onValueChangeFinished = {},
                    )
                }
                Row {
                    Text(
                        text = voice.duration,
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = voice.recordTime,
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun PlaylistPagePreview() {
    VoiceRecorderTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PlaylistContent(
                listOf(
                    Voice("title", "", isPlaying = false, "00:01"),
                    Voice("title2", "", isPlaying = true, "00:10"),
                    Voice("title3", "", isPlaying = false, "02:21"),
                    Voice("title4", "", isPlaying = false, "05:01"),
                    Voice("title5", "", isPlaying = false, "00:41")
                ),
                onPlayPause = {},
                onStop = {},
                onVoiceClicked = { i, voice ->
                },
                onBackPressed = {},
                progress = 0.0f,
                duration = 0.0f,
                onProgressChange = {},

                )
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

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PlaylistItemPreview() {
    VoiceRecorderTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PlaylistItem(
                voice =
                Voice(
                    title = "title prview",
                    path = "path",
                    isPlaying = false,
                    duration = "00:12",
                    recordTime = "just now"
                ),
                onVoiceClicked = {},
                onStop = {},
                modifier = Modifier,
                progress = 0f,
                duration = 0f,
                onProgressChange = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MediaControlsPreview() {
    VoiceRecorderTheme {
        Surface(color = MaterialTheme.colorScheme.background) {

        }
    }
}