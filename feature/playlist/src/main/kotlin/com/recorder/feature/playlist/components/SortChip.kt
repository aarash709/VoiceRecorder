package com.recorder.feature.playlist.components

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
import com.core.common.model.SortOrder

@Composable
internal fun DateSortChip(
	modifier: Modifier = Modifier,
	sortOrder: SortOrder,
	onSetSortOrder: (SortOrder) -> Unit
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
				label = { Text("Duration") },
				border = BorderStroke(0.dp, Color.Transparent)
			)
		}
		DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
			DropdownMenuItem(
				text = { Text("Ascending") },
				onClick = { onSetSortOrder(SortOrder.ByRecordingDuration) }
			)
			DropdownMenuItem(
				text = { Text("Descending") },
				onClick = { onSetSortOrder(SortOrder.ByRecordingDuration) }
			)
		}
	}
}

@Composable
internal fun NameSortChip(
	modifier: Modifier = Modifier,
	sortOrder: SortOrder,
	onSetSortOrder: (SortOrder) -> Unit
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
				label = { Text("Date") }, border = BorderStroke(0.dp, Color.Transparent)
			)
		}
		DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
			DropdownMenuItem(
				text = { Text("Recent") },
				onClick = { onSetSortOrder(SortOrder.ByRecordingDate) }
			)
			DropdownMenuItem(
				text = { Text("Oldest") },
				onClick = { onSetSortOrder(SortOrder.ByRecordingDate) }
			)
		}
	}
}

