package com.recorder.feature.playlist

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recorder.core.designsystem.theme.VoiceRecorderTheme

@Composable
fun PlaylistButtonBar(
    showRenameButton: Boolean,
    selectedVoices: Set<String>,
    showRenameSheet: (Boolean) -> Unit,
    renameTextFieldValue: (TextFieldValue) -> Unit,
    delete: (Set<String>) -> Unit,
) {
    BottomAppBar(
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        contentPadding = PaddingValues(bottom = 8.dp),
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp)
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { delete(selectedVoices) }
                    .padding(8.dp)) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Icon"
                )
                Text("Delete", fontSize = 10.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { }
                    .padding(8.dp)) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Outlined.SdStorage,
                    contentDescription = "Save Button"
                )
                Text("Save", fontSize = 10.sp)
            }
            AnimatedVisibility(
                visible = showRenameButton,
                enter = fadeIn() + slideInHorizontally { it },
                exit = fadeOut() + slideOutHorizontally { it }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val value =
                                selectedVoices.first()
                            renameTextFieldValue(
                                TextFieldValue(
                                    text = value,
                                    selection = TextRange(value.length)
                                )
                            )
                            showRenameSheet(true)
                        }
                        .padding(8.dp)) {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Outlined.DriveFileRenameOutline,
                        contentDescription = "Rename Button"
                    )
                    Text("Rename", fontSize = 10.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomBarPreview() {
    VoiceRecorderTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PlaylistButtonBar(
                showRenameButton = true,
                selectedVoices = setOf(),
                showRenameSheet = {},
                renameTextFieldValue = {},
                delete = {})
        }
    }
}