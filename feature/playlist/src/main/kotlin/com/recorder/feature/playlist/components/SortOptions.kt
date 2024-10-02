package com.recorder.feature.playlist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.core.common.model.SortByDurationOptions

@Composable
internal fun SortOptions(
	isSortByName: Boolean,
	sortedByDuration: SortByDurationOptions,
	onSetSortByName: () -> Unit,
	onSetByDurationChange: (SortByDurationOptions) -> Unit
) {
	Row(
		modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(16.dp)
	) {
		FilterChip(
			selected = isSortByName,
			onClick = { onSetSortByName() },
			leadingIcon = {
				AnimatedVisibility(isSortByName) {
					Icon(
						imageVector = Icons.Default.Done,
						contentDescription = null
					)
				}
			},
			label = { Text("Name") }
		)
		DurationSortChip(
			sortedByDuration = sortedByDuration,
			onSetByDurationChange = onSetByDurationChange
		)
//		DateSortChip(sortOrder = isSortByName, onSetSortOrder = onSetSortByName)
	}
}