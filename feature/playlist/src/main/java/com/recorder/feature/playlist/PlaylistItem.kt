package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import timber.log.Timber

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    voice: Voice,
    progress: Float,
    duration: Float,
    isInEditMode: Boolean,
    isSelected: Boolean,
    onProgressChange: (Float) -> Unit,
    onVoiceClicked: (Voice) -> Unit,
    onStop: () -> Unit,
) {
    Surface(
        modifier = Modifier.animateContentSize(),
        onClick = {
            if (!voice.isPlaying) {
                onVoiceClicked(voice)
                Timber.e("onclick")
            }
            else {
                onStop()
            }
        },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        val textColor = if (voice.isPlaying) MaterialTheme.colorScheme.primary
        else LocalContentColor.current
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
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
                        color = textColor,
                        fontSize = 16.sp
                    )
                    Row {
                        Text(
                            text = voice.recordTime,
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = voice.isPlaying,
                    label = "play icon"
                ) {
//                    Row {
//                        Icon(
//                            imageVector = Icons.Default.GraphicEq,
//                            tint = MaterialTheme.colorScheme.onSurface,
//                            modifier = Modifier
//                                .size(60.dp)
//                                .padding(all = 8.dp)
//                                .clip(CircleShape)
//                                .clickable {
//
//                                },
//                            contentDescription = ""
//                        )
                        Slider(
                            value = progress,
                            onValueChange = { onProgressChange(it) },
                            modifier = Modifier,
                            valueRange = 0f..duration,
                            steps = 0,
                            onValueChangeFinished = {},
                        )
                        AnimatedVisibility(
                            voice.isPlaying,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                        }
//                    }

                }
            }
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = voice.duration,
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(end = 8.dp)
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
            onVoiceClicked = {},
            onStop = {},
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
            onVoiceClicked = {},
            onStop = {},
        )
    }
}