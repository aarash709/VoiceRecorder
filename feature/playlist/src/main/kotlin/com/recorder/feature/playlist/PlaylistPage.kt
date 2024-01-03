package com.recorder.feature.playlist

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun Playlist(
    onBackPressed: () -> Unit,
) {
    val viewModel = hiltViewModel<PlaylistViewModel>()
    val playerState = rememberPlayerState()
    val context = LocalContext.current
    var playingVoiceIndex by remember {
        mutableIntStateOf(-1)
    }
    val voiceList by viewModel.voices.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit, block = {
        viewModel.getVoices(context)
    })
    val isPlaying by playerState.isVoicePlaying.collectAsStateWithLifecycle()
    val progress by playerState.progress.collectAsStateWithLifecycle()
    val duration by playerState.voiceDuration.collectAsStateWithLifecycle()
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
            playerState.browser?.run {
                seekTo(lastProgress.toLong())
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PlaylistContent(
            voices = voiceList,
            onPlayPause = { playerState.browser?.run { pause() } },
            onStop = { playerState.browser?.run { stop() } },
            onVoiceClicked = { voiceIndex, voice ->
                Timber.e("on voice clicked")
                playingVoiceIndex = voiceIndex
                val metadata = MediaMetadata.Builder()
                    .setTitle(voice.title)
                    .setIsPlayable(true).build()
                val mediaItem = MediaItem.Builder()
                    .setMediaMetadata(metadata)
                    .setUri(voice.path)
                    .setMediaId(voice.title)
                    .build()
                playerState.browser?.run {
                    Timber.e("item id to play:${mediaItem.mediaId}")
                    setMediaItem(mediaItem)
                    play()
                }
            },
            onBackPressed = { onBackPressed() },
            progress = progress,
            duration = duration,
            onProgressChange = { desireePosition ->
                lastProgress = desireePosition
            },
            onDeleteVoices = { titles ->
                //can delete multiple
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
    progress: Float,
    duration: Float,
    onProgressChange: (Float) -> Unit,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onVoiceClicked: (Int, Voice) -> Unit,
    onBackPressed: () -> Unit,
    onDeleteVoices: (Set<String>) -> Unit,
    onSaveVoiceFile: () -> Unit,
    rename: (current: String, desired: String) -> Unit,
) {
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
            //make sure there is no duplicate selected voice
            selectedVoices += voices.map { it.title }
        } else {
            selectedVoices = emptySet()
        }
    }
    BackHandler(isInEditMode) {
        if (isInEditMode) {
            selectedVoices = emptySet()
        }
    }
    Scaffold(
        modifier = Modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            PlaylistTopBar(
                isInEditMode = isInEditMode,
                selectedVoices = selectedVoices,
                scrollBehavior = scrollBehavior,
                onIsAllSelected = { isAllSelected = !isAllSelected },
                onSelectedVoiceUpdate = { selectedVoices = emptySet() },
                onBackPressed = { onBackPressed() },
            )
        },
        bottomBar = {
            PlaylistBottomBar(
                isInEditMode = isInEditMode,
                showRenameButton = showRenameButton,
                selectedVoices = selectedVoices,
                onShowRenameSheet = { showRenameSheet = it },
                renameTextFieldValue = { renameTextFieldValue = it },
                onDeleteVoices = { onDeleteVoices(it) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
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
            if (voices.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Recordings",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = voices,
                        key = { index, _ ->
                            index
                        }) { index, voice ->
                        val selected by remember(voices) {
                            derivedStateOf {
                                voice.title in selectedVoices
                            }
                        }
                        PlaylistItem(
                            modifier =
                            if (isInEditMode) {
                                Modifier.clickable {
                                    if (selected)
                                        selectedVoices -= voice.title
                                    else selectedVoices += voice.title
                                }
                            } else {
                                Modifier.combinedClickable(
                                    onLongClick = {
                                        if (!voice.isPlaying) selectedVoices += voices[index].title
                                    },
                                    onClick = {
                                        if (!voice.isPlaying) {
                                            onVoiceClicked(index, voice)
                                            Timber.e("onclick")
                                        } else {
                                            onStop()
                                        }
                                    }
                                )
                            },
                            voice = voice,
                            onStop = { onStop() },
                            progress = progress,
                            duration = duration,
                            isInEditMode = isInEditMode,
                            isSelected = selected,
                            onProgressChange = { progress ->
                                onProgressChange(progress)
                            },
                            onPause = { onPlayPause() },
                        )
                    }
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
                VoicesSampleData,
                onPlayPause = {},
                onStop = {},
                onVoiceClicked = { i, voice ->
                },
                onBackPressed = {},
                progress = 0.0f,
                duration = 0.0f,
                onProgressChange = {},
                onDeleteVoices = {},
                onSaveVoiceFile = {},
                rename = { s1, s2 -> },
            )
        }
    }
}

val VoicesSampleData = listOf(
    Voice("title", "", isPlaying = false, "00:01"),
    Voice("title2", "", isPlaying = true, "00:10"),
    Voice("title3", "", isPlaying = false, "02:21"),
    Voice("title4", "", isPlaying = false, "05:01"),
    Voice("title5", "", isPlaying = false, "00:41"),
    Voice("title6", "", isPlaying = false, "08:01"),
    Voice("title7", "", isPlaying = false, "10:05"),
    Voice("title", "", isPlaying = false, "00:01"),
    Voice("title2", "", isPlaying = false, "00:10"),
    Voice("title3", "", isPlaying = false, "02:21"),
    Voice("title4", "", isPlaying = false, "05:01"),
    Voice("title5", "", isPlaying = false, "00:41"),
    Voice("title6", "", isPlaying = false, "08:01"),
    Voice("title7", "", isPlaying = false, "10:05")
)