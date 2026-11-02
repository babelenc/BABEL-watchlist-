package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cinemora_prefs")

class PreferencesManager(private val context: Context) {
    companion object {
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_HAPTIC_ENABLED = booleanPreferencesKey("haptic_feedback")
        val KEY_ANIMATIONS_ENABLED = booleanPreferencesKey("animations_enabled")
        val KEY_SEARCH_HISTORY = stringPreferencesKey("search_history")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_THEME] ?: true
    }

    val isHapticEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_HAPTIC_ENABLED] ?: true
    }

    val areAnimationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ANIMATIONS_ENABLED] ?: true
    }

    val searchHistory: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_SEARCH_HISTORY] ?: ""
        if (raw.isBlank()) emptyList() else raw.split("||").filter { it.isNotBlank() }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_THEME] = enabled
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun setAnimationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ANIMATIONS_ENABLED] = enabled
        }
    }

    suspend fun addSearchQuery(query: String) {
        val clean = query.trim()
        if (clean.isBlank()) return
        context.dataStore.edit { prefs ->
            val raw = prefs[KEY_SEARCH_HISTORY] ?: ""
            val list = raw.split("||").filter { it.isNotBlank() }.toMutableList()
            list.remove(clean)
            list.add(0, clean)
            prefs[KEY_SEARCH_HISTORY] = list.take(15).joinToString("||")
        }
    }

    suspend fun clearSearchHistory() {
        context.dataStore.edit { prefs ->
            prefs[KEY_SEARCH_HISTORY] = ""
        }
    }
}
