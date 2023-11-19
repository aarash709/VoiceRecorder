package com.recorder.feature.playlist

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChecklistRtl
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun Playlist(
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
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
    var lastProgress by remember(progress) {
        mutableFloatStateOf(progress)
    }
    LaunchedEffect(key1 = isPlaying) {
        viewModel.updateVoiceList(
            selectedVoiceIndex = playingVoiceIndex,
            isPlaying = isPlaying
        )
    }
    LaunchedEffect(key1 = lastProgress) {
        if (progress != lastProgress) {
            delay(50)
            onProgressChange(lastProgress)
        }
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
            progress = lastProgress,
            duration = duration,
            onProgressChange = { desireePosition ->
                lastProgress = desireePosition
            },
            delete = {},
            save = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    delete: () -> Unit,
    save: () -> Unit,
) {
    var voice by remember {
        mutableStateOf(Voice())
    }
    var selectedVoices by remember {
        mutableStateOf(emptySet<String>())
    }
    val isInEditMode by remember {
        derivedStateOf { selectedVoices.isNotEmpty() }
    }
    var isAllSelected by remember(selectedVoices) {
        mutableStateOf(
            if (selectedVoices.isNotEmpty())
                voices.size == selectedVoices.size
            else
                false
        )
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(key1 = isAllSelected) {
        Timber.e("is all: $isAllSelected")
        if (isAllSelected) {
            //make sure there is no duplicate selected voice
            selectedVoices = emptySet()
            selectedVoices += voices.map { it.title }
        } else {
            selectedVoices = emptySet()
        }
    }
    Scaffold(
        modifier = Modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MediumTopAppBar(
                title = {
                    AnimatedContent(
                        targetState = isInEditMode,
                        label = "Title Animation"
                    ) { inEditMode ->
                        if (inEditMode)
                            Text(text = "${selectedVoices.count()} item selected")
                        else {
                            Text(
                                text = "Recordings",
                            )
                        }
                    }
                },
                navigationIcon = {
                    AnimatedContent(
                        targetState = isInEditMode,
                        label = "Top bar Icon"
                    ) { isInEditMode ->
                        if (isInEditMode) {
                            IconButton(onClick = { selectedVoices = emptySet() }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = "Clear selection Button"
                                )
                            }
                        } else {
                            IconButton(onClick = { onBackPressed() }) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowBack,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    contentDescription = "back icon"
                                )
                            }
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(visible = isInEditMode) {
                        IconButton(onClick = { isAllSelected = !isAllSelected }) {
                            Icon(
                                imageVector = Icons.Outlined.ChecklistRtl,
                                contentDescription = "Select all button"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults
                    .mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isInEditMode,
                enter = slideInVertically(
                    initialOffsetY = { height ->
                        height
                    }) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { height ->
                        height
                    }
                ) + fadeOut()
            ) {
                BottomAppBar(
                    modifier = Modifier,
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    contentPadding = PaddingValues(bottom = 8.dp),
                    windowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { delete() }) {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                imageVector = Icons.Outlined.DeleteForever,
                                contentDescription = "Delete Icon"
                            )
                        }
                        IconButton(onClick = { save() }) {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                imageVector = Icons.Outlined.Save,
                                contentDescription = "Save Button"
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = voices.size,
                    key = {
                        it
                    }) { voiceIndex ->
                    val selected by remember() {
                        derivedStateOf {
                            voices[voiceIndex].title in selectedVoices
                        }
                    }
                    PlaylistItem(
                        modifier = if (isInEditMode) {
                            Modifier.clickable {
                                if (selected)
                                    selectedVoices -= voices[voiceIndex].title
                                else selectedVoices += voices[voiceIndex].title
                            }
                        } else {
                            Modifier.combinedClickable(
                                onLongClick = {
                                    selectedVoices += voices[voiceIndex].title
                                },
                                onClick = { }
                            )
                        },
                        voice = voices[voiceIndex],
                        onVoiceClicked = { clickedVoice ->
                            onVoiceClicked(voiceIndex, clickedVoice)
                            voice = clickedVoice
                        },
                        onStop = { onStop() },
                        progress = progress,
                        duration = duration,
                        isInEditMode = isInEditMode,
                        isSelected = selected,
                        onProgressChange = { progress ->
                            onProgressChange(progress)
                        }
                    )
                }
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
    isInEditMode: Boolean,
    isSelected: Boolean,
    onProgressChange: (Float) -> Unit,
    onVoiceClicked: (Voice) -> Unit,
    onStop: () -> Unit,
) {
    Surface(
        modifier = Modifier.animateContentSize(),
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(
                    targetState = voice.isPlaying,
                    label = "play icon"
                ) { isPlaying ->
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
                    modifier = Modifier.animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),//janky animation if set to > 0
                ) {
                    Text(
                        text = voice.title,
                        color = textColor
                    )
                    AnimatedVisibility(
                        voice.isPlaying,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
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
            if (isInEditMode) {
                RadioButton(selected = isSelected, onClick = { /*TODO*/ })
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
                onProgressChange = {}, delete = {}, save = {},

                )
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
                isInEditMode = true,
                isSelected = false,
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