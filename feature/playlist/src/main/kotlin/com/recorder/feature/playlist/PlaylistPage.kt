package com.recorder.feature.playlist

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme

@Composable
fun Playlist(
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<PlaylistViewModel>()
    val recorderViewModel = hiltViewModel<RecordViewModel>()
    val isRecording by recorderViewModel.isRecording.collectAsStateWithLifecycle()
    val recordingTimer by recorderViewModel.formattedTimer.collectAsStateWithLifecycle()
    val playerState = rememberPlayerState()
    val browser = playerState.browser

    val voiceList by viewModel.voices.collectAsStateWithLifecycle()
    val isPlaying by playerState.isVoicePlaying.collectAsStateWithLifecycle()
    val progress by playerState.progress.collectAsStateWithLifecycle()
    val duration by playerState.voiceDuration.collectAsStateWithLifecycle()

    var playingVoiceIndex by rememberSaveable(isPlaying, playerState.browser?.currentPosition) {
        mutableIntStateOf(
            if (isPlaying) {
                voiceList.indexOf(voiceList.firstOrNull { it.title == playerState.browser?.currentMediaItem?.mediaId })
            } else {
                -1
            }
        )
    }
    LaunchedEffect(key1 = isPlaying, playerState.browser?.currentPosition) {
        if (isPlaying && voiceList.isNotEmpty()) {
            viewModel.updateVoiceList(
                selectedVoiceIndex = playingVoiceIndex,
                isPlaying = true
            )
        } else {
            viewModel.getVoices(context)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PlaylistContent(
            voices = voiceList,
            onPausePlayback = { browser?.run { pause() } },
            onRecord = { recorderViewModel.onRecord(context) },
            onStopPlayback = { browser?.run { stop() } },
            onStartPlayback = { voiceIndex, voice ->
                playingVoiceIndex = voiceIndex
                val metadata = MediaMetadata.Builder()
                    .setTitle(voice.title)
                    .setIsPlayable(true).build()
                val mediaItem = MediaItem.Builder()
                    .setMediaMetadata(metadata)
                    .setUri(voice.path)
                    .setMediaId(voice.title)
                    .build()
                browser?.run {
                    setMediaItem(mediaItem)
                    play()
                }
            },
            onBackPressed = { onBackPressed() },
            isRecording = isRecording,
            recordingTimer = recordingTimer,
            progressSeconds = progress,
            duration = if (duration > 0f) duration else 0f,
            onProgressChange = { _ ->
            },
            onDeleteVoices = { titles ->
                viewModel.deleteVoice(titles.toList(), context)
            },
            onSaveVoiceFile = {
                // TODO: implement save functionality
                //save to shared storage: eg. recording or music or downloads folder
            },
            rename = { current, desired ->
                viewModel.renameVoice(current, desired, context)
                viewModel.getVoices(context) //refresh list
            },
        )
    }
}


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun PlaylistContent(
    voices: List<Voice>,
    progressSeconds: Long,
    duration: Float,
    isRecording: Boolean,
    recordingTimer: String,
    onProgressChange: (Float) -> Unit,
    onRecord: () -> Unit,
    onPausePlayback: () -> Unit,
    onStopPlayback: () -> Unit,
    onStartPlayback: (Int, Voice) -> Unit,
    onBackPressed: () -> Unit,
    onDeleteVoices: (Set<String>) -> Unit,
    onSaveVoiceFile: () -> Unit,
    rename: (current: String, desired: String) -> Unit,
) {
    var selectedVoices by remember {
        mutableStateOf(emptySet<String>())
    }
    var selectedVoice by remember {
        mutableStateOf("")
    }
    val isInSelectionMode by remember {
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
    val focusRequester = remember {
        FocusRequester()
    }
    var showRenameSheet by rememberSaveable {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()
    var renameTextFieldValue by remember() {
        mutableStateOf(TextFieldValue(""))
    }
    val showRenameButton by rememberSaveable(selectedVoices) {
        mutableStateOf(selectedVoices.size == 1)
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(key1 = Unit, block = {
        sheetState.hide()
    })
    LaunchedEffect(key1 = isAllSelected) {
        if (isAllSelected) {
            selectedVoices += voices.map { it.title }
        } else {
            selectedVoices = emptySet()
        }
    }
    BackHandler(isInSelectionMode) {
        if (isInSelectionMode) {
            selectedVoices = emptySet()
        }
    }
    Scaffold(
        modifier = Modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            PlaylistTopBar(
                isInEditMode = isInSelectionMode,
                selectedVoices = selectedVoices,
                scrollBehavior = scrollBehavior,
                onIsAllSelected = { isAllSelected = !isAllSelected },
                onSelectedVoiceUpdate = { selectedVoices = emptySet() },
                onBackPressed = { onBackPressed() },
            )
        },
        bottomBar = {
            if (isInSelectionMode)
                PlaylistBottomBar(
                    isInEditMode = true,
                    showRenameButton = showRenameButton,
                    selectedVoices = selectedVoices,
                    onShowRenameSheet = { showRenameSheet = it },
                    renameTextFieldValue = { renameTextFieldValue = it },
                    onDeleteVoices = { onDeleteVoices(it) })
            else
                BottomAppBar(
                    actions = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isRecording)
                                Icon(
                                    imageVector = Icons.Filled.Stop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.dp,
                                            color = Color.LightGray,
                                            shape = CircleShape
                                        )
                                        .clickable { onRecord() }
                                        .size(50.dp),
                                    tint = Color.Red.copy(green = 0.2f),
                                    contentDescription = "Recorder icon"
                                )
                            else
                                Icon(
                                    imageVector = Icons.Filled.Circle,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.dp,
                                            color = Color.LightGray,
                                            shape = CircleShape
                                        )
                                        .clickable { onRecord() }
                                        .size(50.dp),
                                    tint = Color.Red.copy(green = 0.2f),
                                    contentDescription = "Recorder icon"
                                )
                        }
                    },
                    tonalElevation = 0.dp
                )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            var showRecordingSheet by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(key1 = isRecording) {
                showRecordingSheet = isRecording
            }
            if (showRenameSheet) {
                PlaylistBottomSheet(
                    focusRequester = focusRequester,
                    sheetState = sheetState,
                    selectedVoices = selectedVoices,
                    showRenameSheet = { showRenameSheet = it },
                    renameTextFieldValue = renameTextFieldValue,
                    onTextFieldValueChange = { renameTextFieldValue = it },
                    rename = { current, desired ->
                        rename(current, desired)
                        selectedVoices = emptySet()
                    }

                )
            }
            if (showRecordingSheet) {
                RecordingBottomSheet(
                    recordingTimer = recordingTimer,
                    title = "Now Recording",
                    sheetState = sheetState,
                    showRecordingSheet = { showRecordingSheet = it },
                    onRecord = { onRecord() })
            }
            if (voices.isEmpty()) {
                EmptyListMessage()
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = voices,
                        key = { index, _ -> index }) { index, voice ->
                        val isExpanded by remember(voices) {
                            derivedStateOf {
                                selectedVoice == voice.title
                            }
                        }
                        val isSelected by remember(voices) {
                            derivedStateOf {
                                voice.title in selectedVoices
                            }
                        }
                        PlaylistItem(
                            modifier =
                            if (isInSelectionMode) {
                                Modifier.clickable {
                                    if (isSelected)
                                        selectedVoices -= voice.title
                                    else selectedVoices += voice.title
                                }
                            } else {
                                Modifier.combinedClickable(
                                    onLongClick = {
                                        if (!voice.isPlaying) {
                                            selectedVoice = "" //shrink item first
                                            selectedVoices += voices[index].title
                                        }
                                    },
                                    onClick = {
                                        selectedVoice = if (selectedVoice == voice.title) {
                                            Voice().title //empty string; shrinks current expanded item
                                        } else {
                                            voice.title
                                        }
                                    }
                                )
                            },
                            voice = voice,
                            progressSeconds = progressSeconds,
                            duration = duration,
                            shouldExpand = isExpanded,
                            isSelected = isSelected,
                            onProgressChange = { progress ->
                                onProgressChange(progress)
                            },
                            onPlay = { item -> onStartPlayback(index, item) },
                            onStop = { onStopPlayback() },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingBottomSheet(
    recordingTimer: String,
    title: String = "Now Recording",
    sheetState: SheetState,
    showRecordingSheet: (Boolean) -> Unit,
    onRecord: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {
            showRecordingSheet(false)
        },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 20.sp
            )
            Text(text = recordingTimer, modifier = Modifier.padding(vertical = 8.dp))
            Icon(
                imageVector = Icons.Filled.Stop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = CircleShape
                    )
                    .clickable { onRecord() },
                tint = Color.Red.copy(green = 0.2f),
                contentDescription = "Recorder icon"
            )
        }
    }
}

@Composable
fun EmptyListMessage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tap on record button to add a voice recording",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun PlaylistPagePreview() {
    VoiceRecorderTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PlaylistContent(
                VoicesSampleData,
                onPausePlayback = {},
                onRecord = {},
                onStopPlayback = {},
                onStartPlayback = { _, _ ->
                },
                onBackPressed = {},
                progressSeconds = 0,
                duration = 0.0f,
                isRecording = true,
                recordingTimer = "00:01",
                onProgressChange = {},
                onDeleteVoices = {},
                onSaveVoiceFile = {},
                rename = { _, _ -> },
            )
        }
    }
}

val VoicesSampleData = listOf(
    Voice("title", "", isPlaying = false, "00:01"),
    Voice("title2", "", isPlaying = false, "00:10"),
    Voice("title3", "", isPlaying = false, "02:21"),
    Voice("title4", "", isPlaying = false, "05:01"),
    Voice("title5", "", isPlaying = false, "00:41"),
    Voice("title6", "", isPlaying = false, "08:01"),
    Voice("title7", "", isPlaying = false, "10:05"),
    Voice("title8", "", isPlaying = false, "00:01"),
    Voice("title9", "", isPlaying = false, "00:10"),
    Voice("title10", "", isPlaying = false, "02:21"),
    Voice("title11", "", isPlaying = false, "05:01"),
    Voice("title12", "", isPlaying = false, "00:41"),
    Voice("title13", "", isPlaying = false, "08:01"),
    Voice("title14", "", isPlaying = false, "10:05")
)