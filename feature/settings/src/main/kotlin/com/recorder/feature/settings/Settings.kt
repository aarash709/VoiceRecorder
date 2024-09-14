package com.recorder.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
        onSetFormat = settingsViewModel::setRecorderFormat,
        onSetQuality = settingsViewModel::setRecorderQuality,
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
    onSetFormat: (RecordingFormat) -> Unit,
    onSetQuality: (RecordingQuality) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRecentlyDeleted: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val currentRecordingFormat by remember(uiState) {
        val value = when (uiState.recordingFormat) {
            RecordingFormat.Mp4 -> context.resources.getString(R.string.mp4)
        }
        mutableStateOf(value)
    }
    val quality by remember(uiState) {
        val value = when (uiState.recordingQuality) {
            RecordingQuality.Low -> context.resources.getString(R.string.low)
            RecordingQuality.Standard -> context.resources.getString(R.string.standard)
            RecordingQuality.High -> context.resources.getString(R.string.high)
        }
        mutableStateOf(value)
    }
    val backgroundColor = MaterialTheme.colorScheme.background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = backgroundColor),
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
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = backgroundColor,
                    scrolledContainerColor = backgroundColor
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
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
                        RecorderFormatOptions(
                            currentRecordingFormat = currentRecordingFormat,
                            onOptionSelected = { onSetFormat(it) })
                    }
                )
                SettingsItemWithOptions(
                    title = stringResource(id = R.string.recording_quality),
                    currentActiveOption = quality,
                    options = {
                        RecordingQualityOptions(
                            currentRecordingQuality = quality,
                            onSetQuality = { onSetQuality(it) })
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
            onSetFormat = { },
            onSetQuality = { },
            onNavigateBack = {},
            onNavigateToRecentlyDeleted = {})
    }
}