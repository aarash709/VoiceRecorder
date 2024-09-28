package com.recorder.feature.record

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.IBinder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import com.recorder.core.designsystem.theme.components.NavigateToPlaylistButton
import com.recorder.core.designsystem.theme.components.RecorderButton
import com.recorder.service.RecorderService
import com.recorder.service.RecorderService.Companion.RecordingState
import kotlinx.coroutines.delay

@Composable
fun Record(
	isTransitionAnimationRunning: Boolean = false,
	onNavigateToPlaylist: () -> Unit,
) {
	val recorderViewModel: RecordViewModel = hiltViewModel()
	val context = LocalContext.current
	val hapticFeedback = LocalHapticFeedback.current
	val recordingTimer by recorderViewModel.formattedTimer.collectAsStateWithLifecycle()
	var recorderService: RecorderService? by remember {
		mutableStateOf(null)
	}
	val qualitySetting by recorderViewModel.qualitySetting.collectAsStateWithLifecycle()
	var isRecorderServiceBound by remember {
		mutableStateOf(false)
	}
	var isRecording by rememberSaveable {
		mutableStateOf(false)
	}
	var lastRecordTime by rememberSaveable {
		mutableLongStateOf(0)
	}
	val connection = remember {
		object : ServiceConnection {
			override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
				recorderService = (binder as RecorderService.LocalBinder).getRecorderService()
				isRecorderServiceBound = true
				isRecording =
					recorderService?.recordingState == RecordingState.Recording
				lastRecordTime = recorderService?.getRecordingStartMillis() ?: 0L
			}

			override fun onServiceDisconnected(p0: ComponentName?) {
				isRecording =
					recorderService?.recordingState == RecordingState.Recording
				isRecorderServiceBound = false
			}
		}
	}
	DisposableEffect(key1 = LocalLifecycleOwner.current) {
		if (!isRecorderServiceBound) {
			Intent(context, RecorderService::class.java).apply {
				context.bindService(this, connection, Context.BIND_AUTO_CREATE)
			}
		}
		onDispose {
			if (isRecorderServiceBound) {
				context.unbindService(connection)
			}
		}
	}
	LaunchedEffect(isRecorderServiceBound) {
		//updates ui timer on first composition
		recorderViewModel.updateRecordState(
			isRecording = isRecording,
			currentTime = recorderService?.getRecordingStartMillis()
		)
		if (isTransitionAnimationRunning) {
			delay(500)
			hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
			recorderService?.let { service ->
				isRecording = service.recordingState != RecordingState.Recording
				if (isRecording) {
					Intent(context.applicationContext, RecorderService::class.java).apply {
						context.startService(this)
					}
					service.startRecording(context = context)
					service.setRecordingTimer(timeMillis = System.currentTimeMillis())
					recorderViewModel.updateRecordState(
						isRecording = isRecording,
						currentTime = service.recordingStartTimeMillis
					)
				}
			}
		}
	}
	RecordContent(
		modifier = Modifier
			.padding(16.dp),
		isRecording = isRecording,
		recordingTime = recordingTimer,
		quality = qualitySetting,
		onStopRecording = {
			recorderService?.let { service ->
				val recordingState = service.recordingState
				isRecording = recordingState != RecordingState.Recording
				if (!isRecording) {
					service.stopRecording {
						recorderViewModel.updateRecordState(
							isRecording = isRecording,
							currentTime = 0L
						)
					}
					onNavigateToPlaylist()
				}
			}
		}
	)
}

@Composable
fun RecordContent(
	modifier: Modifier,
	isRecording: Boolean,
	recordingTime: String,
	quality: String,
	onStopRecording: () -> Unit,
) {
	val haptics = LocalHapticFeedback.current
	LaunchedEffect(key1 = isRecording) {
		if (isRecording) {
			haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
			delay(100)
			haptics.performHapticFeedback(HapticFeedbackType.LongPress)
		}
	}
	Scaffold(
		modifier = modifier,
		floatingActionButton = {
			RecorderButton(
				modifier = Modifier,
				onStopRecording = { onStopRecording() },
				isRecording = isRecording
			)
		},
		floatingActionButtonPosition = FabPosition.Center
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			RecordingTimer(
				modifier = Modifier
					.fillMaxWidth(),
				recordingTime
			)
			Text(text = quality, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
		}
	}
}


@Composable
fun RecordingTimer(
	modifier: Modifier = Modifier,
	time: String,
) {
	Column(
		modifier = modifier.padding(bottom = 32.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = time,
			fontSize = 70.sp,
			color = MaterialTheme.colorScheme.onSurface
		)
	}
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
fun Prev() {
	VoiceRecorderTheme {
		Surface(color = MaterialTheme.colorScheme.background) {
			RecordContent(
				modifier = Modifier,
				isRecording = false,
				recordingTime = "01",
				quality = "High quality",
				onStopRecording = {}
			)
		}
	}
}