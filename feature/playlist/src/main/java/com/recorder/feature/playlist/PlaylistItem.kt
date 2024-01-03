package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    voice: Voice,
    progress: Float,
    duration: Float,
    isInEditMode: Boolean,
    isSelected: Boolean,
    onProgressChange: (Float) -> Unit,
    onStop: () -> Unit,
    onPause: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .animateContentSize()
            .clip(RoundedCornerShape(16.dp)) then modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        var newSliderValue by remember {
            mutableFloatStateOf(0f)
        }
        val subTextColor = MaterialTheme.colorScheme.onSurface.copy(
            alpha = 1.0f,
            red = .5f,
            green = .5f,
            blue = .5f
        )
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        modifier = Modifier.animateContentSize(),
                        verticalArrangement = Arrangement.spacedBy(0.dp),//janky animation if set to > 0
                    ) {
                        Text(
                            text = voice.title,
                            fontSize = 16.sp
                        )
                        Row {
                            Text(
                                text = voice.recordTime,
                                fontSize = 12.sp,
                                color = subTextColor
                            )
                        }
                    }

                    if (voice.isPlaying) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(CircleShape),
                            contentDescription = ""
                        )
                    }
                }
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = voice.duration,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = subTextColor
                    )
                    AnimatedVisibility(isInEditMode && !voice.isPlaying) {
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
            AnimatedVisibility(visible = voice.isPlaying) {
                Column {
                    // TODO: investigate "check error" crash
//                    Slider(
//                        value = progress,
//                        onValueChange = { newSliderValue = it },
//                        modifier = Modifier
//                            .padding(horizontal = 0.dp),
//                        valueRange = 0f..duration,
//                        steps = 0,
//                        onValueChangeFinished = { onProgressChange(newSliderValue) },
//                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onPause() }) {
                            Icon(
                                imageVector = Icons.Default.PauseCircleOutline,
                                modifier = Modifier.size(50.dp),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "pause icon"
                            )
                        }
                        Spacer(modifier = Modifier.width(24.dp))
                        IconButton(onClick = { onStop() }) {
                            Icon(
                                imageVector = Icons.Outlined.StopCircle,
                                modifier = Modifier.size(50.dp),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "stop icon"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ItemPlayingPreview() {
    VoiceRecorderTheme {
        PlaylistItem(
            voice = VoicesSampleData[1],
            modifier = Modifier,
            progress = 12.13f,
            duration = 14.15f,
            isInEditMode = false,
            isSelected = false,
            onProgressChange = {},
            onStop = {},
            onPause = {},

        )
    }
}

@Preview
@Composable
private fun ItemPreview() {
    VoiceRecorderTheme {
        PlaylistItem(
            voice = VoicesSampleData.first(),
            modifier = Modifier,
            progress = 12.13f,
            duration = 14.15f,
            isInEditMode = false,
            isSelected = false,
            onProgressChange = {},
            onStop = {},
            onPause = {},
        )
    }
}