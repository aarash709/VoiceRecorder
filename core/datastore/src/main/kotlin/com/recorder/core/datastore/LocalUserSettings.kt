package com.recorder.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.core.common.model.RecordingFormat
import com.core.common.model.RecordingQuality
import com.core.common.model.SortOrder
import com.core.common.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LocalUserSettings @Inject constructor(private val dataStore: DataStore<Preferences>) {

    fun getSettings(): Flow<UserSettings> = combine(
        getEarpieceMode(),
        getRenameManuallyMode(),
        getRecorderFormat(),
        getRecorderQuality()
    ) { enableEarpiece, enableNameManually, format, quality ->
        UserSettings(
            shouldUseEarpiece = enableEarpiece,
            shouldNameManually = enableNameManually,
            recordingFormat = format,
            recordingQuality = quality
        )
    }

    private fun getEarpieceMode(): Flow<Boolean> {
        return dataStore.data.map {
            it[EARPIECE_KEY] ?: false
        }
    }

    private fun getRenameManuallyMode(): Flow<Boolean> {
        return dataStore.data.map {
            it[RENAME_MANUALLY_KEY] ?: false
        }
    }

    private fun getRecorderFormat(): Flow<RecordingFormat> {
        return dataStore.data.map {
            val formatString = it[RECORDER_FORMAT_KEY]
            if (formatString != null) {
                Json.decodeFromString<RecordingFormat>(formatString)
            } else {
                RecordingFormat.Mp4
            }
        }
    }

    private fun getRecorderQuality(): Flow<RecordingQuality> {
        return dataStore.data.map {
            val qualityString = it[RECORDER_QUALITY_KEY]
            if (qualityString != null) {
                Json.decodeFromString<RecordingQuality>(qualityString)
            } else {
                RecordingQuality.Standard
            }
        }
    }

    fun getSortOrder(): Flow<SortOrder> {
        return dataStore.data.map {
            val orderByString = it[ORDER_BY_KEY]
            if (orderByString != null) {
                Json.decodeFromString<SortOrder>(orderByString)
            } else {
                SortOrder.ByRecordingDate
            }
        }
    }

    //store values
    suspend fun setEarpieceMode(value: Boolean) {
        dataStore.edit {
            it[EARPIECE_KEY] = value
        }
    }

    suspend fun setRenameManuallyMode(value: Boolean) {
        dataStore.edit {
            it[RENAME_MANUALLY_KEY] = value
        }
    }

    suspend fun setRecorderFormat(value: String) {
        dataStore.edit {
            it[RECORDER_FORMAT_KEY] = value
        }
    }

    suspend fun setRecorderQuality(value: String) {
        dataStore.edit {
            it[RECORDER_QUALITY_KEY] = value
        }
    }

    suspend fun setSortOrder(value: String) {
        dataStore.edit {
            it[ORDER_BY_KEY] = value
        }
    }

    companion object Keys {
        val EARPIECE_KEY = booleanPreferencesKey("EARPIECE_KEY")
        val RENAME_MANUALLY_KEY = booleanPreferencesKey("NAME_MANUALLY_KEY")
        val RECORDER_FORMAT_KEY = stringPreferencesKey("RECORDER_FORMAT_KEY")
        val RECORDER_QUALITY_KEY = stringPreferencesKey("RECORDER_QUALITY_KEY")
        val ORDER_BY_KEY = stringPreferencesKey("ORDER_BY_KEY")
    }
}