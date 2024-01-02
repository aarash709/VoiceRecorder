package com.recorder.feature.record

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import kotlinx.coroutines.delay


@Composable
fun Record(
    onNavigateToPlaylist: () -> Unit,
) {
    val recordViewModel: RecordViewModel = hiltViewModel()
    val recordTime by recordViewModel.formattedTimer.collectAsStateWithLifecycle()
    val isRecording by recordViewModel.isRecording.collectAsStateWithLifecycle()
    val context = LocalContext.current
    RecordContent(
        modifier = Modifier
            .padding(16.dp),
        isRecording = isRecording,
        recordingAllowed = true,
        recordingTime = recordTime.toString(),
        navigateToPlaylistEnabled = true,
        onRecord = { recordViewModel.onRecord(context) },
        onPlayListClicked = { onNavigateToPlaylist() }
    )
}

@Composable
fun RecordContent(
    modifier: Modifier,
    isRecording: Boolean,
    recordingAllowed: Boolean,
    recordingTime: String,
    navigateToPlaylistEnabled: Boolean,
    onRecord: () -> Unit,
    onPlayListClicked: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(key1 = isRecording) {
        if (isRecording) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            delay(100)
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    Box(modifier = Modifier.fillMaxSize() then modifier) {
        //center
        Column(modifier = Modifier.align(Alignment.Center)) {
            AnimatedVisibility(
                visible = isRecording,
            ) {
                RecordingTimer(
                    modifier = Modifier
                        .fillMaxWidth(),
                    recordingTime
                )
            }
            RecordAudioButton(
                modifier = Modifier
                    .fillMaxWidth(),
                recordingAllowed = recordingAllowed,
                isRecording = isRecording
            ) {
                onRecord()
            }
        }
        //bottom
        PlayListButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            navigateToPlaylistEnabled = navigateToPlaylistEnabled
        ) {
            onPlayListClicked()
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
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .75f)
        )
    }
}

@Composable
fun PlayListButton(
    modifier: Modifier = Modifier,
    navigateToPlaylistEnabled: Boolean,
    onListButtonClick: () -> Unit,
) {
    Row(
        modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            onClick = { onListButtonClick() },
            shape = CircleShape,
            enabled = navigateToPlaylistEnabled
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "List of recordings icon",
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RecordAudioButton(
    modifier: Modifier = Modifier,
    recordingAllowed: Boolean,
    isRecording: Boolean,
    startRecording: () -> Unit,
) {
    val circleColor = MaterialTheme.colorScheme.primary
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val circleEffectRadius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = 150),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    val recordingRadius by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val circleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = 150),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedButton(
            onClick = {
                startRecording()
            },
            border = BorderStroke(3.dp, Color.Transparent),
            enabled = recordingAllowed,
            modifier = Modifier
                .size(125.dp)
                .drawWithCache {
                    onDrawBehind {
                        val radius = circleEffectRadius.plus(size.minDimension / 2)
                        val lightRedColor = circleColor.copy(red = 0.4f)
                        if (isRecording) {
                            drawCircle(
                                Brush.radialGradient(
                                    listOf(lightRedColor, Color.Transparent),
                                    radius = recordingRadius,
                                    tileMode = TileMode.Clamp
                                ),
                                radius = size.minDimension / 2
                            )
                        } else {
                            drawCircle(
                                color = circleColor,
                                alpha = 1.0f,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                )
                            )
                            drawCircle(
                                color = circleColor,
                                alpha = circleAlpha,
                                radius = radius,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                ),
                            )
                        }

                    }
                },
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.MicNone,
                contentDescription = "Record Button Icon",
                modifier = Modifier.size(100.dp)
            )
        }
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
                recordingAllowed = true,
                recordingTime = "01",
                navigateToPlaylistEnabled = true,
//        "00",
                onRecord = {},
                onPlayListClicked = {})
        }
    }
}