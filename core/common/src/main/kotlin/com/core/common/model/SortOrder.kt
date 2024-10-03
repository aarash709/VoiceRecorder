package com.core.common.model

import kotlinx.serialization.Serializable

@Serializable
sealed class SortOrder {
	@Serializable
	data object ByName : SortOrder()

	@Serializable
	data object ByRecordingDate : SortOrder()

	@Serializable
	data object ByRecordingDuration : SortOrder()
}

@Serializable
enum class SortByDateOptions() {
	MostRecent,
	Oldest
}

@Serializable
enum class SortByDurationOptions {
	Longest,
	Shortest
}

@Serializable
data class SortByDuration(
	val durationOptions: SortByDurationOptions? = null,
	val isSelected: Boolean = false
)