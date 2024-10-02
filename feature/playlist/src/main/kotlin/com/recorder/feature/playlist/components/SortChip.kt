package com.recorder.feature.playlist.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.core.common.model.SortByDateOptions
import com.core.common.model.SortByDurationOptions

@Composable
internal fun DateSortChip(
	modifier: Modifier = Modifier,
	sortByDateOptions: SortByDateOptions,
	onSetSortByDate: (SortByDateOptions) -> Unit
) {
	Box(modifier = modifier) {
		var expand by remember {
			mutableStateOf(false)
		}
		Row(verticalAlignment = Alignment.CenterVertically) {
			FilterChip(
				selected = true,
				trailingIcon = {
					Icon(
						imageVector = Icons.Default.ArrowDropDown,
						modifier = Modifier.size(16.dp),
						contentDescription = null
					)
				},
				onClick = { expand = true },
				label = { Text(sortByDateOptions.name, modifier = Modifier.animateContentSize()) },
				border = BorderStroke(0.dp, Color.Transparent)
			)
		}
		DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
			DropdownMenuItem(
				text = { Text("MostRecent") },
				onClick = {
					onSetSortByDate(SortByDateOptions.MostRecent)
					expand = false
				}
			)
			DropdownMenuItem(
				text = { Text("Oldest") },
				onClick = {
					onSetSortByDate(SortByDateOptions.Oldest)
					expand = false
				}
			)
		}
	}
}

@Composable
internal fun DurationSortChip(
	modifier: Modifier = Modifier,
	sortedByDuration: SortByDurationOptions,
	onSetByDurationChange: (SortByDurationOptions) -> Unit
) {
	Box(modifier = modifier) {
		var expand by remember {
			mutableStateOf(false)
		}
		Row(verticalAlignment = Alignment.CenterVertically) {
			FilterChip(
				selected = true,
				onClick = { expand = true },
				trailingIcon = {
					Icon(
						imageVector = Icons.Default.ArrowDropDown,
						modifier = Modifier.size(16.dp),
						contentDescription = null
					)
				},
				label = { Text(sortedByDuration.name, modifier = Modifier.animateContentSize()) },
				border = BorderStroke(0.dp, Color.Transparent)
			)
		}
		DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
			DropdownMenuItem(
				text = { Text("Longest") },
				onClick = {
					onSetByDurationChange(SortByDurationOptions.Ascending)
					expand = false
				}
			)
			DropdownMenuItem(
				text = { Text("Shortest") },
				onClick = {
					onSetByDurationChange(SortByDurationOptions.Descending)
					expand = false
				}
			)
		}
	}
}

