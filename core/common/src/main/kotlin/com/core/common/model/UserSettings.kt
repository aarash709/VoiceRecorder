package com.core.common.model

data class UserSettings(
    val shouldUseEarpiece: Boolean = false,
    val shouldNameManually: Boolean = false,
    val recordingFormat: RecordingFormat = RecordingFormat.Mp4,
    val recordingQuality: RecordingQuality = RecordingQuality.Standard,
)

enum class RecordingFormat{
    Mp4,
}
enum class RecordingQuality{
    Low,
    Standard,
    High,
}