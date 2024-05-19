package com.recorder.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.recorder.core.designsystem.theme.VoiceRecorderTheme

@Composable
fun Settings() {
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        uiState = uiState,
        modifier = Modifier,
        onEarpieceMode = { settingsViewModel.setEarpieceMode(!uiState.shouldUseEarpieceSpeaker) },
        onNameRecordingManually = { settingsViewModel.setRenameRecordingManually(uiState.canNameRecordingManually) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    onEarpieceMode: () -> Unit,
    onNameRecordingManually: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Surface(
        modifier = Modifier
            .fillMaxSize()
                then modifier
    ) {
        Column() {
            MediumTopAppBar(
                title = { Text("Settings") },
//            modifier =,
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "back button"
                    )
                },
                actions = {},
//            windowInsets =,
//            colors =,
                scrollBehavior = scrollBehavior
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                SettingsItemWithSwitch(
                    title = "Earpiece mode",
                    subtitle = "Play audio using the earpiece speaker",
                    isChecked = uiState.shouldUseEarpieceSpeaker,
                    onCheckChanged = { onEarpieceMode() }
                )
                SettingsItemWithSwitch(
                    title = "Name recordings manually",
                    isChecked = uiState.canNameRecordingManually,
                    onCheckChanged = { onNameRecordingManually() }
                )
                SettingsItemWithOptions(
                    title = "Recording format",
                    currentOption = "m4a",
                    options = {
                        repeat(3) {
                            Surface(modifier = modifier.fillMaxWidth(.5f)) {
                                Text(text = "$it:text")
                            }
                        }
                    }
                )
                SettingsItemWithOptions(
                    title = "Recording quality",
                    currentOption = "Standard",
                    options = {
                        Surface(modifier = modifier) {
                            Text(text = "Low")
                        }
                        Surface(modifier = modifier) {
                            Text(text = "Standard")
                        }
                        Surface(modifier = modifier) {
                            Text(text = "High")

                        }
                    }
                )
                SettingsItemWithAction(
                    title = "Clear data",
                    action = {}
                )
                SettingsItemWithAction(
                    title = "Recently deleted items",
                    action = {}
                )
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun SettingsPreview() {
    VoiceRecorderTheme {
        val state = SettingsUiState()
        SettingsContent(uiState = state,
            modifier = Modifier,
            onEarpieceMode = { },
            onNameRecordingManually = { })
    }
}