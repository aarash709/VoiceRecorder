package com.recorder.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    val uiState = MutableStateFlow(SettingsUiState())

    internal fun setEarpieceMode(value : Boolean){
        viewModelScope.launch {

        }
    }

    internal fun setNameRecordingManually(value : Boolean){
        viewModelScope.launch {

        }
    }

    internal fun setRecorderFormat(value : RecordingFormat){
        viewModelScope.launch {

        }
    }

    internal fun setRecorderQuality(value : RecordingQuality){
        viewModelScope.launch {

        }
    }

}

data class SettingsUiState(
    val shouldUseEarpieceSpeaker: Boolean = false,
    val canNameRecordingManually: Boolean = false,
    val recordingFormat: RecordingFormat = RecordingFormat.Mp4,
    val recordingQuality: RecordingQuality = RecordingQuality.Standard,
)
enum class RecordingFormat{
    Mp4,
}
enum class RecordingQuality{
    Low,
    Standard,
    High,
}