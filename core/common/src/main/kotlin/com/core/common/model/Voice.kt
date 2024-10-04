package com.core.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Voice(
	val title: String = "",
	val path: String = "",
	val isPlaying: Boolean = false,
	val duration: String = "00:00",
	val recordTime: String = "Just now",
	val recordTimeMillis: Long = 0
)
