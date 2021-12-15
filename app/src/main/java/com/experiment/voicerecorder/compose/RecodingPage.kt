package com.experiment.voicerecorder.compose

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.experiment.voicerecorder.R

@Composable
fun RecodingPage(
    modifier: Modifier,
    recordEnabled: Boolean,
    recordingTime: String,
    navigateToPlaylistEnabled :Boolean,
    onRecord: () -> Unit,
    onListButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        RecordingTimer(modifier = Modifier.fillMaxWidth(),recordingTime)
        RecordAudioButton(modifier = Modifier.fillMaxWidth(),
            recordEnabled
        ) {
            onRecord()
        }

    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        PlayListButton(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            navigateToPlaylistEnabled) {
            onListButtonClick()
        }
    }
}

@Composable
fun RecordingTimer(
    modifier : Modifier = Modifier,
    time: String,
) {
    Column(modifier =  modifier.padding(bottom = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = time,
            fontSize = 40.sp,
            color = MaterialTheme.colors.onSurface)
    }

}

@Composable
fun PlayListButton(
    modifier: Modifier = Modifier,
    navigateToPlaylistEnabled:Boolean,
    onListButtonClick: () -> Unit,
) {
    Row(modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End) {
        TextButton(onClick = { onListButtonClick() },
            shape = CircleShape,
        enabled = navigateToPlaylistEnabled) {
            Icon(
                painter = rememberImagePainter(data = R.drawable.ic_list),
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
    Column(modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(onClick = {
            startRecording()
        },
            border = BorderStroke(0.dp, Color.Transparent),
            enabled = recordEnabled,
            modifier = Modifier.size(125.dp),
            shape = CircleShape) {
            Image(painter = rememberImagePainter(
                data = R.drawable.ic_record),
                contentDescription = "Record Button Icon",
                modifier = Modifier.size(100.dp))
        }
    }
}

@Composable
@Preview(showBackground = false, uiMode = UI_MODE_NIGHT_YES)
fun Prev() {
    RecodingPage(
        modifier = Modifier,
        true,
        "00",
        true,
//        "00",
        onRecord = {},
    ) {

    }
}