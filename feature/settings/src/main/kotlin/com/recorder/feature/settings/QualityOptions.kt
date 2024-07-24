package com.recorder.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.core.common.R
import com.core.common.model.RecordingQuality

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
            isSelected = currentRecordingQuality == stringResource(id = R.string.low),
            isClickable = false,
            onSelectOption = { onSetQuality(RecordingQuality.Low) })
        OptionsItem(
            optionName = stringResource(id = R.string.standard),
            isSelected = currentRecordingQuality == stringResource(id = R.string.standard),
            onSelectOption = { onSetQuality(RecordingQuality.Standard) })
        OptionsItem(
            optionName = stringResource(id = R.string.high),
            isSelected = currentRecordingQuality == stringResource(id = R.string.high),
            isClickable = false,
            onSelectOption = { onSetQuality(RecordingQuality.High) })
    }
}
