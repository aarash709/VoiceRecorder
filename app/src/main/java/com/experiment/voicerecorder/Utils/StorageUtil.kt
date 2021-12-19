package com.experiment.voicerecorder.Utils

import android.content.Context
import android.content.SharedPreferences

class StorageUtil(val context: Context) {

    lateinit var preferences : SharedPreferences

    fun storeVoice(path: String) {
        preferences = context
            .getSharedPreferences(VOICE_STORAGE, Context.MODE_PRIVATE)
            preferences.edit().putString(VOICE,path).apply()
    }
    fun loadVoice() {
        preferences = context
            .getSharedPreferences(VOICE_STORAGE, Context.MODE_PRIVATE)
            preferences.getString(VOICE,null)
    }
    fun clearCach(){
        preferences = context
            .getSharedPreferences(VOICE_STORAGE, Context.MODE_PRIVATE)
        preferences.edit().clear().apply()
    }
}