package com.example.mobile_app_project.data.local

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mobile_app_project.data.repository.model.CityCoordinates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Extension DataStore instance bound to Context
val Context.dataStore by preferencesDataStore(name = "user_prefs")

/**
 * Simple preferences helper for storing last used city name.
 */
class UserPreferences(private val context: Context) {

    private val LAST_CITY_KEY = stringPreferencesKey("last_city_name")
    private val FAVORITES_KEY = stringPreferencesKey("favorite_cities_json")
    private val TEMP_UNIT_KEY = stringPreferencesKey("temperature_unit") // "C" or "F"
    private val WIND_UNIT_KEY = stringPreferencesKey("wind_unit") // "m_s" or "km_h"
    private val RECENT_CITIES_KEY = stringPreferencesKey("recent_cities_json")

    private val json = Json { ignoreUnknownKeys = true }

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

    suspend fun addFavorite(city: CityCoordinates) {
        val current = getFavoritesInternal().toMutableList()
        if (current.none { it.name.equals(city.name, ignoreCase = true) }) {
            current.add(city)
            setFavoritesJson(json.encodeToString(current))
        }
    }

    suspend fun removeFavorite(cityName: String) {
        val current = getFavoritesInternal().filterNot { it.name.equals(cityName, ignoreCase = true) }
        setFavoritesJson(json.encodeToString(current))
    }

    suspend fun clearFavorites() = setFavoritesJson(json.encodeToString(emptyList<CityCoordinates>()))

    fun observeFavoritesList(): Flow<List<CityCoordinates>> = observeFavorites().map { raw ->
        if (raw.isNullOrBlank()) emptyList() else runCatching { json.decodeFromString<List<CityCoordinates>>(raw) }.getOrElse { emptyList() }
    }

    private suspend fun getFavoritesInternal(): List<CityCoordinates> {
        // Read a single snapshot of preferences instead of chaining Flow operators incorrectly
        val prefs = context.dataStore.data.first()
        val raw = prefs[FAVORITES_KEY]
        return if (raw.isNullOrBlank()) {
            emptyList()
        } else {
            runCatching { json.decodeFromString<List<CityCoordinates>>(raw) }.getOrElse { emptyList() }
        }
    }

    // Units
    fun observeTemperatureUnit(): Flow<String> = context.dataStore.data.map { prefs -> prefs[TEMP_UNIT_KEY] ?: "C" }
    suspend fun setTemperatureUnit(unit: String) {
        context.dataStore.edit { it[TEMP_UNIT_KEY] = unit }
    }
    fun observeWindUnit(): Flow<String> = context.dataStore.data.map { prefs -> prefs[WIND_UNIT_KEY] ?: "m_s" }
    suspend fun setWindUnit(unit: String) {
        context.dataStore.edit { it[WIND_UNIT_KEY] = unit }
    }

    // Recent searches (JSON list of strings)
    fun observeRecentCities(): Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[RECENT_CITIES_KEY]
        if (raw.isNullOrBlank()) emptyList() else runCatching { json.decodeFromString<List<String>>(raw) }.getOrElse { emptyList() }
    }
    suspend fun addRecentCity(name: String) {
        val prefs = context.dataStore.data.first()
        val raw = prefs[RECENT_CITIES_KEY]
        val list = if (raw.isNullOrBlank()) mutableListOf<String>() else runCatching { json.decodeFromString<List<String>>(raw) }.getOrElse { mutableListOf() }.toMutableList()
        val filtered = list.filterNot { it.equals(name, ignoreCase = true) }.toMutableList()
        filtered.add(0, name)
        val limited = filtered.take(20)
        context.dataStore.edit { it[RECENT_CITIES_KEY] = json.encodeToString(limited) }
    }
    suspend fun clearRecentCities() { context.dataStore.edit { it[RECENT_CITIES_KEY] = json.encodeToString(emptyList<String>()) } }
}
