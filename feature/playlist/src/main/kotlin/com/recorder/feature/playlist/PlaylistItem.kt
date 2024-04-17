package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    voice: Voice,
    progressSeconds: Long,
    duration: Float,
    shouldExpand: Boolean,
    isSelected: Boolean,
    onProgressChange: (Float) -> Unit,
    onPlay: (Voice) -> Unit,
    onStop: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .animateContentSize()
            .clip(RoundedCornerShape(16.dp))
                then modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SelectionRadioButton(isSelected = isSelected, isPlaying = voice.isPlaying)
                Title(
                    title = voice.title,
                    recordTime = voice.recordTime,
                    duration = voice.duration,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            AnimatedVisibility(visible = shouldExpand) {
                Column {
                    Slider(
                        value = progressSeconds.toFloat(),
                        onValueChange = { onProgressChange(it) },
                        valueRange = 0f..duration,
                    )
                    val progressText = progressSeconds.let { progress ->
                        "${String.format("%02d", progress.seconds.inWholeMinutes)}:" +
                                String.format("%02d", progress.seconds.inWholeSeconds)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = progressText)
                        Text(text = voice.duration)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedContent(
                            targetState = voice.isPlaying,
                            transitionSpec = { fadeIn(tween(0)) togetherWith fadeOut(tween(0)) },
                            label = "Play-Pause"
                        ) { isPlaying ->
                            if (isPlaying) {
                                IconButton(onClick = { onStop() }) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        modifier = Modifier.size(50.dp),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "pause icon"
                                    )
                                }
                            } else {
                                IconButton(onClick = { onPlay(voice) }) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        modifier = Modifier.size(50.dp),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "play icon"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionRadioButton(isSelected: Boolean, isPlaying: Boolean) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(isSelected && !isPlaying) {
            if (isSelected)
                Icon(
                    imageVector = Icons.Default.RadioButtonChecked,
                    contentDescription = null,
                    modifier = Modifier,
                    tint = MaterialTheme.colorScheme.primary
                )
            else
                Icon(
                    imageVector = Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    modifier = Modifier,
                    tint = MaterialTheme.colorScheme.primary
                )
        }
    }
}

@Composable
fun Title(title: String, recordTime: String, color: Color, duration: String) {
    val subTextColor = color.copy(
        alpha = 1.0f,
        red = .5f,
        green = .5f,
        blue = .5f
    )
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(0.dp),//janky animation if set to > 0
        ) {
            Text(
                text = title,
                color = color,
                fontSize = 16.sp
            )
            Text(
                text = recordTime,
                fontSize = 12.sp,
                color = subTextColor
            )
        }
        Text(
            text = duration,
            fontSize = 12.sp,
            color = subTextColor
        )
    }
}

@Preview
@Composable
private fun ListItemPreview() {
    VoiceRecorderTheme {
        PlaylistItem(
            voice = VoicesSampleData.first(),
            modifier = Modifier,
            progressSeconds = 8,
            duration = 12f,
            shouldExpand = false,
            isSelected = true,
            onProgressChange = {},
            onPlay = {},
            onStop = {},
        )
    }
}

@Preview
@Composable
private fun SelectedItemPreview() {
    VoiceRecorderTheme {
        PlaylistItem(
            voice = VoicesSampleData.first().copy(isPlaying = true),
            modifier = Modifier,
            progressSeconds = 13,
            duration = 18f,
            shouldExpand = true,
            isSelected = false,
            onProgressChange = {},
            onPlay = {},
            onStop = {},
        )
    }
}