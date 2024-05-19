package com.recorder.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.common.model.RecordingFormat
import com.core.common.model.RecordingQuality
import com.recorder.core.datastore.LocalUserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val userSettingsData: LocalUserSettings) :
    ViewModel() {

    val uiState = userSettingsData.getSettings().map { settings ->
        Timber.e(settings.toString())
        SettingsUiState(
            shouldUseEarpieceSpeaker = settings.shouldUseEarpiece,
            canNameRecordingManually = settings.shouldNameManually,
            recordingFormat = settings.recordingFormat,
            recordingQuality = settings.recordingQuality
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    internal fun setEarpieceMode(value: Boolean) {
        viewModelScope.launch {
            userSettingsData.setEarpieceMode(value)
        }
    }

    internal fun setRenameRecordingManually(value: Boolean) {
        viewModelScope.launch {
            userSettingsData.setRenameManuallyMode(value)
        }
    }

    internal fun setRecorderFormat(value: RecordingFormat) {
        viewModelScope.launch {
            val stringValue = Json.encodeToString(value)
            userSettingsData.setRecorderFormat(stringValue)
        }
    }

    internal fun setRecorderQuality(value: RecordingQuality) {
        viewModelScope.launch {
            val stringValue = Json.encodeToString(value)
            userSettingsData.setRecorderQuality(stringValue)
        }
    }

}

data class SettingsUiState(
    val shouldUseEarpieceSpeaker: Boolean = false,
    val canNameRecordingManually: Boolean = false,
    val recordingFormat: RecordingFormat = RecordingFormat.Mp4,
    val recordingQuality: RecordingQuality = RecordingQuality.Standard,
)