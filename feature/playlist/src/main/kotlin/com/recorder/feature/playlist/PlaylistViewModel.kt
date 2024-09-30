package com.recorder.feature.playlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core.common.Storage
import com.core.common.model.SortOrder
import com.core.common.model.Voice
import com.recorder.core.datastore.LocalUserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
	private val storage: Storage,
	private val dataStore: LocalUserSettings
) : ViewModel() {

	private val _voices = MutableStateFlow(listOf<Voice>())
	val voices = _voices.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(1_000),
		initialValue = listOf()
	)

	val sortOrder = getSortOrder().stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5_000),
		SortOrder.ByRecordingDate
	)

	fun getVoices(context: Context) {
		viewModelScope.launch {
			_voices.update { storage.getVoices(context) ?: listOf() }
		}
	}

	fun updateVoiceList(selectedVoiceIndex: Int, isPlaying: Boolean = false) {
		viewModelScope.launch {
			_voices.update { voices ->
				voices.mapIndexed { index, voice ->
					when {
						index == selectedVoiceIndex && isPlaying -> {
							voice.copy(isPlaying = true)
						}

						index == selectedVoiceIndex && !isPlaying -> {
							voice.copy(isPlaying = false)
						}

						else -> voice.copy(isPlaying = false)
					}
				}
			}
		}
	}

	fun deleteVoice(voiceTitle: List<String>, context: Context) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				storage.deleteVoice(voiceTitle = voiceTitle, context = context)
				getVoices(context)
			}
		}
	}

	fun renameVoice(currentName: String, newName: String, context: Context) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val isRenamed = storage.renameVoice(
					currentName = currentName,
					newName = newName,
					context = context
				)
				if (isRenamed)
					getVoices(context)
			}
		}
	}

	private fun getSortOrder(): Flow<SortOrder> {
		return dataStore.getSortOrder()
	}

	fun setSortOrder(orderBy: SortOrder) {
		viewModelScope.launch {
			val value = Json.encodeToString(orderBy)
			dataStore.setSortOrder(value)
		}
	}
}

