package com.example.mobile_app_project.data.local

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
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
    private val FAVORITES_KEY = stringPreferencesKey("favorite_cities_json")

    suspend fun saveLastCityName(name: String) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[LAST_CITY_KEY] = name
        }
    }

    fun observeLastCityName(): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[LAST_CITY_KEY]
        }

    // Favorites stored as JSON array of CityCoordinates
    fun observeFavorites(): Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[FAVORITES_KEY] }

    suspend fun setFavoritesJson(json: String) {
        context.dataStore.edit { prefs: MutablePreferences ->
            prefs[FAVORITES_KEY] = json
        }
    }
}
