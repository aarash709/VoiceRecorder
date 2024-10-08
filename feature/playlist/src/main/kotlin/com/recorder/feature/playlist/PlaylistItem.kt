package com.recorder.feature.playlist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core.common.model.Voice
import com.recorder.core.designsystem.theme.VoiceRecorderTheme
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    voice: Voice,
    isPlaying: Boolean,
    progressSeconds: Long,
    duration: Float,
    shouldExpand: Boolean,
    isSelected: Boolean,
    isInSelectionMode: Boolean,
    onPlay: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekBack: () -> Unit,
    onStop: () -> Unit,
    onDeleteVoice: (voiceTitle: String) -> Unit,
    onPlaybackOptions: () -> Unit,
    onItemActions: () -> Unit,
) {
//    val isPlaying = isPlaying
    Surface(
        modifier = Modifier
            .animateContentSize()
            .clip(RoundedCornerShape(16.dp))
                then modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        val subTextColor = MaterialTheme.colorScheme.onSurface.copy(
            alpha = 1.0f,
            red = .5f,
            green = .5f,
            blue = .5f
        )
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SelectionRadioButton(
                        isSelected = isSelected,
                        isInSelectionMode = isInSelectionMode,
                        isPlaying = isPlaying
                    )
                    Title(
                        title = voice.title,
                        recordTime = voice.recordTime,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (!shouldExpand) {
                    Text(
                        text = voice.duration,
                        fontSize = 12.sp,
                        color = subTextColor
                    )
                } else {
                    IconButton(onClick = { onItemActions() }, enabled = !isPlaying) {
                        Icon(
                            imageVector = Icons.Outlined.Pending,
                            modifier = Modifier.size(28.dp),
                            contentDescription = "voice item actions"
                        )
                    }
                }
            }
            AnimatedVisibility(visible = shouldExpand) {
                val progress = if (isPlaying) progressSeconds else 0L
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    PlayerSlider(value = progress.toFloat(), duration = duration)
                    val progressText = progress.let { progress ->
                        "${
                            String.format(
                                Locale.ENGLISH,
                                "%02d",
                                progress.seconds.inWholeMinutes
                            )
                        }:" +
                                String.format(
                                    Locale.ENGLISH,
                                    "%02d",
                                    progress.seconds.inWholeSeconds
                                )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val fontSize = 12.sp
                        val color =
                            if (!isPlaying) subTextColor else MaterialTheme.colorScheme.onSurface
                        Text(text = progressText, fontSize = fontSize, color = color)
                        Text(text = voice.duration, fontSize = fontSize, color = color)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onPlaybackOptions() }, enabled = !isPlaying) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                modifier = Modifier.size(28.dp),
                                contentDescription = "playback options icon"
                            )
                        }
                        PlaybackControls(isPlaying = isPlaying, onStop = onStop, onPlay = onPlay,
                            onSeekForward = { onSeekForward() },
                            onSeekBack = { onSeekBack() })
                        IconButton(onClick = { onDeleteVoice(voice.title) }, enabled = !isPlaying) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                modifier = Modifier.size(28.dp),
                                contentDescription = "delete icon"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerSlider(value: Float, duration: Float) {
    Box(Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f))
        )
        Box(
            Modifier
                .fillMaxWidth(value / duration)
                .height(5.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    onStop: () -> Unit,
    onPlay: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekBack: () -> Unit,
) {
    AnimatedContent(
        targetState = isPlaying,
        transitionSpec = { fadeIn(tween(0)) togetherWith fadeOut(tween(0)) },
        label = "Play-Pause"
    ) { isPlaying ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = { onSeekBack() }, enabled = isPlaying) {
                Icon(
                    imageVector = Icons.Default.Replay10,
                    modifier = Modifier.size(28.dp),
                    contentDescription = "forward 10 seconds icon"
                )
            }
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
                IconButton(onClick = { onPlay() }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "play icon"
                    )
                }
            }
            IconButton(onClick = { onSeekForward() }, enabled = isPlaying) {
                Icon(
                    imageVector = Icons.Default.Forward10,
                    modifier = Modifier.size(28.dp),
                    contentDescription = "forward 10 seconds icon"
                )
            }
        }
    }
}

@Composable
fun SelectionRadioButton(isInSelectionMode: Boolean, isSelected: Boolean, isPlaying: Boolean) {
    AnimatedVisibility(
        isInSelectionMode && !isPlaying,
        enter = fadeIn(animationSpec = tween(25)) + expandHorizontally(),
        exit = fadeOut(animationSpec = tween(25)) + shrinkHorizontally(),
    ) {
        val modifier = Modifier.padding(end = 8.dp)
        if (isSelected)
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = modifier,
                tint = MaterialTheme.colorScheme.primary
            )
        else
            Icon(
                imageVector = Icons.Filled.RadioButtonUnchecked,
                contentDescription = null,
                modifier = modifier,
                tint = MaterialTheme.colorScheme.primary
            )
    }
}

@Composable
fun Title(
    title: String,
    modifier: Modifier = Modifier,
    recordTime: String,
    color: Color,
) {
    val subTextColor = color.copy(
        alpha = 1.0f,
        red = .5f,
        green = .5f,
        blue = .5f
    )
    Column(
        modifier = modifier,
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
}

@PreviewLightDark
@Composable
private fun ListItemPreview() {
    VoiceRecorderTheme {
        PlaylistItem(
            voice = VoicesSampleData.first(),
            isPlaying = false,
            modifier = Modifier,
            progressSeconds = 8,
            duration = 12f,
            shouldExpand = false,
            isSelected = true,
            onPlay = {},
            onStop = {},
            onDeleteVoice = {},
            onPlaybackOptions = {},
            onItemActions = {},
            isInSelectionMode = true,
            onSeekForward = {},
            onSeekBack = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun SelectedPlayingItemPreview() {
    VoiceRecorderTheme {
        PlaylistItem(
            voice = VoicesSampleData.first().copy(isPlaying = true),
            isPlaying = true,
            modifier = Modifier,
            progressSeconds = 13,
            duration = 18f,
            shouldExpand = true,
            isSelected = false,
            onPlay = {},
            onStop = {},
            onDeleteVoice = {},
            onPlaybackOptions = {},
            onItemActions = {},
            isInSelectionMode = false,
            onSeekForward = {},
            onSeekBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun SelectedNotPlayingItemPreview() {
    VoiceRecorderTheme {
        PlaylistItem(
            voice = VoicesSampleData.first().copy(isPlaying = false),
            isPlaying = false,
            modifier = Modifier,
            progressSeconds = 13,
            duration = 18f,
            shouldExpand = true,
            isSelected = false,
            onPlay = {},
            onStop = {},
            onDeleteVoice = {},
            onPlaybackOptions = {},
            onItemActions = {},
            isInSelectionMode = false,
            onSeekForward = {},
            onSeekBack = {},
        )
    }
}