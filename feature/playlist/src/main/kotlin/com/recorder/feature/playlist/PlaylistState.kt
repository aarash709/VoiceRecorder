package com.recorder.feature.playlist

import com.core.common.model.SortByDateOptions
import com.core.common.model.SortByDurationOptions
import com.core.common.model.Voice


internal data class PlaylistUiState(
	val voices: Voice,
	val isLoading: Boolean = false,
	val sortByName: Boolean = false,
	val sortByDurationOption: SortByDateOptions = SortByDateOptions.MostRecent,
	val sortByDataOption: SortByDurationOptions = SortByDurationOptions.Ascending
)