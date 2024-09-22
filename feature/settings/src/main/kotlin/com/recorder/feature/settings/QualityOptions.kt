package com.recorder.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.core.common.R
import com.core.common.model.RecordingQuality
import com.recorder.core.designsystem.theme.VoiceRecorderTheme

@Composable
internal fun RecordingQualityOptions(
    currentRecordingQuality: String,
    onSetQuality: (RecordingQuality) -> Unit,
) {
    Column(
        modifier = Modifier,
    ) {
        OptionsItem(
            optionName = stringResource(id = R.string.low),
            description = "~64 kbps",
            isSelected = currentRecordingQuality == stringResource(id = R.string.low),
            onSelectOption = { onSetQuality(RecordingQuality.Low) })
        OptionsItem(
            optionName = stringResource(id = R.string.standard),
            description = "~128 kbps",
            isSelected = currentRecordingQuality == stringResource(id = R.string.standard),
            onSelectOption = { onSetQuality(RecordingQuality.Standard) })
        OptionsItem(
            optionName = stringResource(id = R.string.high),
            description = "~192 kbps",
            isSelected = currentRecordingQuality == stringResource(id = R.string.high),
            onSelectOption = { onSetQuality(RecordingQuality.High) })
    }
}

@PreviewLightDark
@Composable
fun PreviewRecordingOptions(modifier: Modifier = Modifier) {
   VoiceRecorderTheme {
       RecordingQualityOptions("Standard") {  }
   }
}
