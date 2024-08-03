package com.recorder.core.designsystem.theme.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp


@Composable
fun RecorderButton(
    modifier: Modifier = Modifier,
    onRecord: () -> Unit,
    isRecording: Boolean,
) {
    Row(
        modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            modifier = Modifier
                .size(60.dp),
            onClick = { onRecord() }) {
            if (!isRecording) {
                Icon(
                    imageVector = Icons.Filled.Circle,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = CircleShape
                        ),
                    tint = Color.Red.copy(green = 0.2f),
                    contentDescription = "start recording icon"
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Stop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = CircleShape
                        ),
                    tint = Color.Red.copy(green = 0.2f),
                    contentDescription = "stop recording icon"
                )
            }
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
fun NavigateToPlaylistButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
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
            enabled = isEnabled
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
