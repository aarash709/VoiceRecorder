package com.recorder.feature.playlist.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingBottomSheet(
    recordingTimer: String,
    title: String = "Now Recording",
    sheetState: SheetState,
    showRecordingSheet: (Boolean) -> Unit,
    onRecord: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {
            showRecordingSheet(false)
        },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 20.sp
            )
            Text(text = recordingTimer, modifier = Modifier.padding(vertical = 8.dp))
            Icon(
                imageVector = Icons.Filled.Stop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = CircleShape
                    )
                    .clickable { onRecord() },
                tint = Color.Red.copy(green = 0.2f),
                contentDescription = "Recorder icon"
            )
        }
    }
}