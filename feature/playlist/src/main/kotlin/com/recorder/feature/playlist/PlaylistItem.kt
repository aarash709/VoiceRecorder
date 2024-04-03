package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core.common.model.Voice
import com.google.android.material.slider.Slider
import com.recorder.core.designsystem.theme.VoiceRecorderTheme

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    voice: Voice,
    progress: Float,
    isSelected: Boolean,
    onProgressChange: (Float) -> Unit,
    onPause: () -> Unit,
//    isInEditMode: Boolean,
//    duration: Float,
//    onStop: () -> Unit,
) {
    val subTextColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = 1.0f,
        red = .5f,
        green = .5f,
        blue = .5f
    )
    Surface(
        modifier = Modifier
            .animateContentSize()
            .clip(RoundedCornerShape(16.dp))
                then modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Title(
                title = voice.title,
                recordTime = voice.recordTime,
                color = MaterialTheme.colorScheme.onSurface
            )
            AnimatedVisibility(visible = isSelected) {
                Column {
                    Slider(value = progress, onValueChange = { onProgressChange(it) })
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedContent(
                            targetState = voice.isPlaying,
                            label = "Play-Pause"
                        ) { isPlaying ->
                            if (isPlaying) {
                                IconButton(onClick = { onPause() }) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        modifier = Modifier.size(50.dp),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "pause icon"
                                    )
                                }
                            } else {
                                IconButton(onClick = { onPause() }) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        modifier = Modifier.size(50.dp),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "pause icon"
                                    )
                                }
                            }

                        }
                    }
                }
            }
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Row(
//                    modifier = Modifier,
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    AnimatedVisibility(isInEditMode && !voice.isPlaying) {
//                        if (isSelected)
//                            Icon(
//                                imageVector = Icons.Default.RadioButtonChecked,
//                                contentDescription = null,
//                                modifier = Modifier,
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        else
//                            Icon(
//                                imageVector = Icons.Default.RadioButtonUnchecked,
//                                contentDescription = null,
//                                modifier = Modifier,
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                    }
//                }
//            }
//            AnimatedVisibility(visible = voice.isPlaying) {
//                Column {
//                    // TODO: investigate "check error" crash when animating a slider
////                    Slider(
////                        value = progress,
////                        onValueChange = { newSliderValue = it },
////                        modifier = Modifier
////                            .padding(horizontal = 0.dp),
////                        valueRange = 0f..duration,
////                       steps = 0,
////                        onValueChangeFinished = { onProgressChange(newSliderValue) },
//                    )
//
//                }
//            }
        }
    }
}

@Composable
fun Title(title: String, recordTime: String, color: Color) {
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
//        val duration = if (voice.isPlaying) "${progress}/${voice.duration}" else voice.duration
        Text(
            text = "time",
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
            progress = 12f,
            isSelected = false,
            onProgressChange = {},
            onPause = {},
//            isInEditMode = false,
//            onStop = {},
//            duration = 14.15f,
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
            progress = 13f,
            isSelected = true,
            onProgressChange = {},
            onPause = {},
//            duration = 14.15f,
//            isInEditMode = false,
//            onStop = {},
        )
    }
}