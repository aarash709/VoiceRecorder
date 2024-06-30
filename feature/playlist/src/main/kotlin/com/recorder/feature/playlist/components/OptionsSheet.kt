package com.recorder.feature.playlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsSheet(
    modifier: Modifier = Modifier,
    playbackSpeed: Float,
    onDismissRequest: () -> Unit,
    onPlaybackSpeedChange: (Float) -> Unit,
) {
    var sliderValue by remember {
        mutableFloatStateOf(1f)
    }
    var isSkipSilenceEnabled by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = playbackSpeed) {
        sliderValue = playbackSpeed
    }
    val isDefaultSetting by remember(sliderValue, isSkipSilenceEnabled) {
        mutableStateOf(sliderValue != 1.0f || isSkipSilenceEnabled)
    }
    ModalBottomSheet(onDismissRequest = { onDismissRequest() }) {
        Surface(modifier = modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            sliderValue = 1.0f
                            isSkipSilenceEnabled = false
                        },
                        colors = ButtonDefaults
                            .textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                        enabled = isDefaultSetting
                    ) {
                        Text(text = "Reset", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(text = "Options", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { onDismissRequest() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "close options sheet icon"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    Column {
                        Text(text = "Playback speed", fontSize = 14.sp)
                        Row(
                            modifier = Modifier.padding(horizontal = 0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = sliderValue,
                                onValueChange = {
                                    sliderValue = it
                                },
                                onValueChangeFinished = { onPlaybackSpeedChange(sliderValue) },
                                steps = 2,
                                valueRange = 0.5f..2.0f
                            )
                        }
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Skip Silence", fontSize = 16.sp)
                        Switch(
                            checked = isSkipSilenceEnabled,
                            onCheckedChange = { isSkipSilenceEnabled = it })
                    }
                }
            }
        }

    }
}

@Preview
@Composable
private fun SheetPrev() {
    OptionsSheet(
        onDismissRequest = {},
        onPlaybackSpeedChange = { },
        modifier = Modifier,
        playbackSpeed = 0.0f
    )
}