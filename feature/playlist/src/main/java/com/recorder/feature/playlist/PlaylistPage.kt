package com.recorder.feature.playlist

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChecklistRtl
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
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
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import kotlinx.coroutines.delay

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
            delete = { titles ->
                //can delete multiple
                viewModel.deleteVoice(titles.toList(), context)

            },
            save = {
                //save to shared storage: eg. recording or music or downloads folder
            },
            rename = { current, desired ->
                viewModel.renameVoice(current, desired, context)
                viewModel.getVoices(context)
            },
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
    delete: (Set<String>) -> Unit,
    save: () -> Unit,
    rename: (current: String, desired: String) -> Unit,
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
            selectedVoices = emptySet()
            selectedVoices += voices.map { it.title }
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
                PlaylistButtonBar(
                    showRenameButton = showRenameButton,
                    selectedVoices = selectedVoices,
                    showRenameSheet = { showRenameSheet = it },
                    renameTextFieldValue = {
                        renameTextFieldValue = it
                    },
                    delete = {
                        delete(it)
                        selectedVoices = emptySet()
                    })
            }
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
                    items(
                        count = voices.size,
                        key = {
                            it
                        }) { voiceIndex ->
                        val selected by remember(voices) {
                            derivedStateOf {
                                voices[voiceIndex].title in selectedVoices
                            }
                        }
                        PlaylistItem(
                            modifier =
                            if (isInEditMode) {
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
                delete = {},
                save = {},
                rename = { s1, s2 -> },
            )
        }
    }
}