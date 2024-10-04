package com.recorder.feature.playlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.core.common.model.SortByDateOptions
import com.core.common.model.SortByDuration

@Composable
internal fun SortOptions(
	sortedByDuration: SortByDuration,
	sortedByDateOptions: SortByDateOptions,
	onSetByDuration: (SortByDuration) -> Unit,
	onSetByDate: (SortByDateOptions) -> Unit
) {
	Row(
		modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(16.dp)
	) {
		DateSortChip(
			sortByDateOptions = sortedByDateOptions,
			onSetSortByDate = onSetByDate
		)
		DurationSortChip(
			sortedByDuration = sortedByDuration,
			onSetByDurationChange = onSetByDuration
		)
	}
}