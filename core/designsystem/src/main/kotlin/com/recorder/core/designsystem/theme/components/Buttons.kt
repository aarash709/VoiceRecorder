package com.recorder.core.designsystem.theme.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun RecorderButton(
	modifier: Modifier = Modifier,
	onStopRecording: () -> Unit,
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
            onClick = { onStopRecording() }) {
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
