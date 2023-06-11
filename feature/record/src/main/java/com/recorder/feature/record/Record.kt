package com.recorder.feature.record

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun Record() {
    val recordViewModel : RecordViewModel = hiltViewModel()
    val recordTime = recordViewModel.recordTime.collectAsStateWithLifecycle().value
    val context = LocalContext.current.applicationContext
    RecordContent(
        modifier = Modifier,
        recordEnabled = true,
        recordingTime = recordTime.toString(),
        navigateToPlaylistEnabled = false,
        onRecord = { recordViewModel.onRecord(context) },
        onListButtonClick = { })

}

@Composable
fun RecordContent(
    modifier: Modifier,
    recordEnabled: Boolean,
    recordingTime: String,
    navigateToPlaylistEnabled: Boolean,
    onRecord: () -> Unit,
    onListButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        RecordingTimer(modifier = Modifier.fillMaxWidth(), recordingTime)
        RecordAudioButton(
            modifier = Modifier.fillMaxWidth(),
            recordEnabled
        ) {
            onRecord()
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        PlayListButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            navigateToPlaylistEnabled = navigateToPlaylistEnabled
        ) {
            onListButtonClick()
        }
    }
}

@Composable
fun RecordingTimer(
    modifier: Modifier = Modifier,
    time: String,
) {
    Column (
        modifier = modifier.padding(bottom = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time,
            fontSize = 40.sp,
            color = MaterialTheme.colors.onSurface
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
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
fun RecordAudioButton(
    modifier: Modifier = Modifier,
    recordEnabled: Boolean,
    startRecording: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            onClick = {
                startRecording()
            },
            border = BorderStroke(0.dp, Color.Transparent),
            enabled = recordEnabled,
            modifier = Modifier.size(125.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
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
    RecordContent(
        modifier = Modifier,
        true,
        "00",
        true,
//        "00",
        onRecord = {},
    ) {

    }
}