package com.recorder.feature.playlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheet(
    focusRequester: FocusRequester,
    sheetState: SheetState,
    selectedVoices: Set<String>,
    showRenameSheet: (Boolean) -> Unit,
    renameTextFieldValue: TextFieldValue,
    onTextFieldValueChange: (TextFieldValue) -> Unit,
    rename: (current: String, desired: String) -> Unit,
) {
    LaunchedEffect(key1 = Unit, block = {
        focusRequester.requestFocus()
    })
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        sheetState = sheetState,
        onDismissRequest = { showRenameSheet(false) }) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Rename", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                modifier = Modifier.focusRequester(focusRequester = focusRequester),
                value = renameTextFieldValue,
                onValueChange = { onTextFieldValueChange(it) })
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            showRenameSheet(false)
                            onTextFieldValueChange(TextFieldValue(text = ""))
                        }
                    }) {
                    Text(text = "Cancel")
                }
                Button(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    onClick = {
                        rename(
                            selectedVoices.first(),
                            renameTextFieldValue.text
                        )
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            showRenameSheet(false)
                        }

                    }) {
                    Text(text = "Ok")
                }
            }
        }
    }
}
