package com.recorder.feature.playlist

import com.core.common.model.SortByDateOptions
import com.core.common.model.SortByDuration
import com.core.common.model.Voice


data class PlaylistUiState(
	val voices: List<Voice> = listOf(),
	val isLoading: Boolean = false,
	val sortByDateOption: SortByDateOptions = SortByDateOptions.MostRecent,
	val sortByDurationOption: SortByDuration = SortByDuration()
)