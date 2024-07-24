package com.recorder.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core.common.R
import com.core.common.model.RecordingFormat
import com.core.common.model.RecordingQuality
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
        onNavigateBack = { onNavigateBack() },
        onNavigateToRecentlyDeleted = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
    onEarpieceMode: (Boolean) -> Unit,
    onNameRecordingManually: (Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRecentlyDeleted: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val currentRecordingFormat by remember {
        val value = when (uiState.recordingFormat) {
            RecordingFormat.Mp4 -> context.resources.getString(R.string.mp4)
        }
        mutableStateOf(value)
    }
    val quality by remember {
        val value = when (uiState.recordingQuality) {
            RecordingQuality.Low -> context.resources.getString(R.string.low)
            RecordingQuality.Standard -> context.resources.getString(R.string.standard)
            RecordingQuality.High -> context.resources.getString(R.string.high)
        }
        mutableStateOf(value)
    }
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
                    currentActiveOption = currentRecordingFormat,
                    options = {
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OptionsItem(
                                optionName = currentRecordingFormat,
                                isSelected = currentRecordingFormat == stringResource(
                                    id = R.string.mp4
                                )
                            ) {}
                        }
                    }
                )
                SettingsItemWithOptions(
                    title = stringResource(id = R.string.recording_quality),
                    currentActiveOption = quality,
                    options = {
                        Column(
                            modifier = Modifier,
                        ) {
                            OptionsItem(optionName = "Low", isSelected = false) {}
                            OptionsItem(optionName = "Standard", isSelected = true) {}
                            OptionsItem(optionName = "High", isSelected = false) {}
                        }
                    }
                )
                SettingsItemWithAction(
                    title = stringResource(id = R.string.clear_data),
                    action = {}
                )
                SettingsItemWithAction(
                    title = stringResource(id = R.string.recently_deleted_items),
                    action = { onNavigateToRecentlyDeleted() }
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
            onNavigateBack = {},
            onNavigateToRecentlyDeleted = {})
    }
}