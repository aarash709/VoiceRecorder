package com.recorder.feature.playlist

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.core.common.model.SortByDateOptions
import com.core.common.model.SortByDuration
import com.core.common.model.SortByDurationOptions
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.LocalSharedTransitionScope
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import com.recorder.feature.playlist.components.EmptyListMessage
import com.recorder.feature.playlist.components.OptionsSheet
import com.recorder.feature.playlist.components.PlaylistBottomSheet
import com.recorder.feature.playlist.components.SortOptions
import kotlin.time.Duration.Companion.seconds

@Composable
fun Playlist(
	onNavigateToSettings: () -> Unit,
	onNavigateToRecorder: () -> Unit,
	onBackPressed: () -> Unit,
) {
	val context = LocalContext.current
	val playlistViewModel = hiltViewModel<PlaylistViewModel>()
	val voiceList by playlistViewModel.voices.collectAsStateWithLifecycle()
	val state by playlistViewModel.uiState.collectAsStateWithLifecycle()

	val playerState = rememberPlayerState()
	val browser = playerState.browser
	val isPlaying by playerState.isVoicePlaying.collectAsStateWithLifecycle()
	val progress by playerState.progress.collectAsStateWithLifecycle()
	val duration by playerState.voiceDuration.collectAsStateWithLifecycle()

	var playingVoiceIndex by rememberSaveable(
		isPlaying,
		playerState.browser?.currentPosition
	) {
		mutableIntStateOf(
			if (isPlaying) {
//				state.voices.indexOfFirst {
//					it.title == playerState.browser?.currentMediaItem?.mediaId
//				}
				voiceList.indexOf(voiceList.firstOrNull { it.title == playerState.browser?.currentMediaItem?.mediaId })
			} else {
				-1
			}
		)
	}
	LaunchedEffect(
		key1 = isPlaying,
		playerState.browser?.currentPosition,
		state
	) {
		if (isPlaying && voiceList.isNotEmpty()) {
			playlistViewModel.updateVoiceList(
				selectedVoiceIndex = playingVoiceIndex,
				isPlaying = true
			)
		} else {
			playlistViewModel.getVoices(context)
		}
	}
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		PlaylistContent(
			voices = voiceList,
			isPlaying = isPlaying,
			progressSeconds = progress,
			playbackSpeed = playerState.browser?.playbackParameters?.speed ?: 1.0f,
			duration = if (duration > 0f) duration else 0f,
			sortByDuration = state.sortByDurationOption,
			sortByDate = state.sortByDateOption,
			onPlaybackSpeedChange = { speedFactor ->
				browser?.run {
					if (!isPlaying) {
						setPlaybackSpeed(speedFactor)
					}
				}
			},
			onSeekForward = {
				if (isPlaying) {
					browser?.run {
						val seekPosition = currentPosition + 10.0.seconds.inWholeMilliseconds
						seekTo(seekPosition)
					}
				}
			},
			onSeekBack = {
				if (isPlaying) {
					browser?.run {
						val seekPosition = currentPosition - 10.0.seconds.inWholeMilliseconds
						seekTo(seekPosition)
					}
				}
			},
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
			onNavigateToSettings = { onNavigateToSettings() },
			onNavigateToRecorder = { onNavigateToRecorder() },
			onBackPressed = { onBackPressed() },
			onDeleteVoices = { titles ->
				playlistViewModel.deleteVoice(titles.toList(), context)
			},
			onSaveVoiceFile = {
				// TODO: implement save functionality
				//save to shared storage: eg. recording or music or downloads folder
			},
			rename = { current, desired ->
				playlistViewModel.renameVoice(current, desired, context)
			},
			onSetSortByDuration = { playlistViewModel.setDurationSort(it) },
			onSetSortByDate = { playlistViewModel.setDateSort(it) }
		)
	}
}


@OptIn(
	ExperimentalMaterial3Api::class,
	ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun PlaylistContent(
	voices: List<Voice>,
	isPlaying: Boolean,
	progressSeconds: Long,
	playbackSpeed: Float,
	duration: Float,
	sortByDuration: SortByDuration,
	sortByDate: SortByDateOptions,
	onPlaybackSpeedChange: (Float) -> Unit,
	onSeekForward: () -> Unit,
	onSeekBack: () -> Unit,
	onStopPlayback: () -> Unit,
	onStartPlayback: (Int, Voice) -> Unit,
	onNavigateToSettings: () -> Unit,
	onNavigateToRecorder: () -> Unit,
	onBackPressed: () -> Unit,
	onDeleteVoices: (Set<String>) -> Unit,
	onSaveVoiceFile: () -> Unit,
	rename: (current: String, desired: String) -> Unit,
	onSetSortByDuration: (SortByDuration) -> Unit,
	onSetSortByDate: (SortByDateOptions) -> Unit,
) {
	val sharedElementScope = LocalSharedTransitionScope.current
		?: throw IllegalStateException("no shared element scope found")
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
	with(sharedElementScope) {
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
					onNavigateToSettings = { onNavigateToSettings() },
					onBackPressed = { onBackPressed() },
				)
			},
			floatingActionButton = {
				if (
					!isInSelectionMode
				) {
					Icon(
						imageVector = Icons.Filled.Circle,
						modifier = Modifier
							.padding(bottom = 40.dp)
							.clip(CircleShape)
							.border(
								width = 1.dp,
								color = Color.LightGray,
								shape = CircleShape
							)
							.size(60.dp)
							.clickable { if (!isPlaying) onNavigateToRecorder() },
						tint = Color.Red.copy(green = 0.2f),
						contentDescription = "Recorder icon"
					)
				}
			},
			floatingActionButtonPosition = FabPosition.Center,
			bottomBar = {
				PlaylistBottomBar(
					isInEditMode = isInSelectionMode,
					showRenameButton = showRenameButton,
					selectedVoices = selectedVoices,
					onShowRenameSheet = { showRenameSheet = it },
					renameTextFieldValue = { renameTextFieldValue = it },
					onDeleteVoices = {
						onDeleteVoices(it)
						selectedVoices = emptySet()
					}
				)
			}
		) { paddingValues ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues)
					.nestedScroll(scrollBehavior.nestedScrollConnection),
			) {
				var showPlayItemOptionsSheet by remember {
					mutableStateOf(false)
				}
				if (showPlayItemOptionsSheet) {
					OptionsSheet(
						onDismissRequest = { showPlayItemOptionsSheet = false },
						playbackSpeed = playbackSpeed,
						onPlaybackSpeedChange = { onPlaybackSpeedChange(it) }
					)
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
				SortOptions(
					sortedByDuration = sortByDuration,
					sortedByDateOptions = sortByDate,
					onSetByDuration = onSetSortByDuration,
					onSetByDate = onSetSortByDate,
				)
				//sort items
				val list by remember(voices, sortByDate, sortByDuration) {
					val sortedByDate = voices
						.sortedWith(
							comparator = when (sortByDate) {
								SortByDateOptions.MostRecent -> compareByDescending { it.recordTimeMillis }
								SortByDateOptions.Oldest -> compareBy { it.recordTimeMillis }
							}
						)
					val sortedByDateAndDuration = if (sortByDuration.isSelected) {
						sortedByDate
							.sortedWith(
								comparator =
								when (sortByDuration.durationOptions) {
									SortByDurationOptions.Longest -> compareByDescending { it.duration }
									SortByDurationOptions.Shortest -> compareBy { it.duration }
									null -> compareBy { it.duration }
								})
					} else {
						sortedByDate
					}
					mutableStateOf(sortedByDateAndDuration)
				}
				if (list.isEmpty()) {
					EmptyListMessage()
				} else {
					LazyColumn(
						modifier = Modifier
							.fillMaxSize()
							.navigationBarsPadding(),
						contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 116.dp),
						verticalArrangement = Arrangement.spacedBy(8.dp)
					) {
						itemsIndexed(
							items = list,
							key = { index, _ -> index }) { index, voice ->
							val shouldExpand by remember(voices) {
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
											if (!isPlaying) {
												selectedVoice = if (selectedVoice == voice.title) {
													Voice().title //empty string; shrinks current expanded item
												} else {
													voice.title
												}
											}
										}
									)
								},
								voice = voice,
								isPlaying = isPlaying,
								progressSeconds = progressSeconds,
								duration = duration,
								shouldExpand = shouldExpand,
								isSelected = isSelected,
								isInSelectionMode = isInSelectionMode,
								onPlay = {
									onStartPlayback(
										index,
										voice
									)
								},
								onStop = { onStopPlayback() },
								onDeleteVoice = { onDeleteVoices(setOf(it)) },
								onPlaybackOptions = { showPlayItemOptionsSheet = true },
								onItemActions = {},
								onSeekForward = { onSeekForward() },
								onSeekBack = { onSeekBack() },
							)
						}
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
				isPlaying = false,
				progressSeconds = 0,
				playbackSpeed = 0.5f,
				duration = 0.0f,
				sortByDuration = SortByDuration(),
				sortByDate = SortByDateOptions.MostRecent,
				onPlaybackSpeedChange = {},
				onSeekForward = {},
				onSeekBack = {},
				onStopPlayback = {},
				onStartPlayback = { _, _ ->
				},
				onNavigateToSettings = {},
				onNavigateToRecorder = {},
				onBackPressed = {},
				onDeleteVoices = {},
				onSaveVoiceFile = {},
				rename = { _, _ -> },
				onSetSortByDuration = {},
				onSetSortByDate = {}
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