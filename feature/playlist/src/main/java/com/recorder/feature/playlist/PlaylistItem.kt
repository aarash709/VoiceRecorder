package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core.common.model.Voice
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
        onClick = { },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        val textColor = if (voice.isPlaying) MaterialTheme.colorScheme.primary
        else LocalContentColor.current
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(
                    targetState = voice.isPlaying,
                    label = "play icon"
                ) { isPlaying ->
                    if (isPlaying)
                        Icon(
                            imageVector = Icons.Default.StopCircle,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(all = 8.dp)
                                .clip(CircleShape)
                                .clickable { onStop() },
                            contentDescription = ""
                        )
                    else
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(all = 8.dp)
                                .clip(CircleShape)
                                .clickable {
                                    onVoiceClicked(Voice(voice.title, voice.path))
                                    Timber.e("ui item: ${voice.title}")
                                },
                            contentDescription = ""
                        )
                }
                Column(
                    modifier = Modifier.animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),//janky animation if set to > 0
                ) {
                    Text(
                        text = voice.title,
                        color = textColor
                    )
                    AnimatedVisibility(
                        voice.isPlaying,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Slider(
                            value = progress,
                            onValueChange = { onProgressChange(it) },
                            modifier = Modifier,
                            valueRange = 0f..duration,
                            steps = 0,
                            onValueChangeFinished = {},
                        )
                    }
                    Row {
                        Text(
                            text = voice.duration,
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = voice.recordTime,
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            if (isInEditMode) {
                RadioButton(selected = isSelected, onClick = { /*TODO*/ })
            }
        }

    }
}