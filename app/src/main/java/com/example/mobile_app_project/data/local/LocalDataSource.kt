package com.example.mobile_app_project.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension DataStore instance bound to Context
val Context.dataStore by preferencesDataStore(name = "user_prefs")

/**
 * Simple preferences helper for storing last used city name.
 */
class UserPreferences(private val context: Context) {

    private val LAST_CITY_KEY = stringPreferencesKey("last_city_name")

    suspend fun saveLastCityName(name: String) {
        context.dataStore.edit { prefs: Preferences ->
            prefs[LAST_CITY_KEY] = name
        }
    }

    fun observeLastCityName(): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[LAST_CITY_KEY]
        }
}
