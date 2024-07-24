package com.recorder.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.core.common.R
import com.core.common.model.RecordingFormat


@Composable
internal fun RecorderFormatOptions(
    currentRecordingFormat: String,
    onOptionSelected: (RecordingFormat) -> Unit,
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OptionsItem(
            optionName = currentRecordingFormat,
            isSelected = currentRecordingFormat == stringResource(
                id = R.string.mp4
            ), onSelectOption = { onOptionSelected(RecordingFormat.Mp4) }
        )
    }
}
