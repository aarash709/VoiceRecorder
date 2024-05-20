package com.recorder.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core.common.R
import com.recorder.core.designsystem.theme.VoiceRecorderTheme

@Composable
fun Settings(onNavigateBack: () -> Unit) {
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(
        uiState = uiState,
        modifier = Modifier,
        onEarpieceMode = settingsViewModel::setEarpieceMode,
        onNameRecordingManually = settingsViewModel::setRenameRecordingManually,
        onNavigateBack = { onNavigateBack() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    onEarpieceMode: (Boolean) -> Unit,
    onNameRecordingManually: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Surface(
        modifier = Modifier
            .fillMaxSize()
                then modifier
    ) {
        Column {
            MediumTopAppBar(
                title = { Text(stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "back button"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                SettingsItemWithSwitch(
                    title = stringResource(id = R.string.earpiece_mode),
                    subtitle = stringResource(id = R.string.earpiece_subtitle),
                    isChecked = uiState.shouldUseEarpieceSpeaker,
                    onCheckChanged = { onEarpieceMode(it) }
                )
                SettingsItemWithSwitch(
                    title = stringResource(id = R.string.name_recordings_manually),
                    isChecked = uiState.canNameRecordingManually,
                    onCheckChanged = { onNameRecordingManually(it) }
                )
                SettingsItemWithOptions(
                    title = stringResource(id = R.string.recording_format),
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
                    title = stringResource(id = R.string.recording_quality),
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
                    title = stringResource(id = R.string.clear_data),
                    action = {}
                )
                SettingsItemWithAction(
                    title = stringResource(id = R.string.recently_deleted_items),
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
            onNameRecordingManually = { },
            onNavigateBack = {})
    }
}