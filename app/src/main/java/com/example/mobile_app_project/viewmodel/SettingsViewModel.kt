package com.example.mobile_app_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_app_project.data.local.UserPreferences
import com.example.mobile_app_project.data.repository.model.CityCoordinates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Skeleton for Settings screen ViewModel.
 */
class SettingsViewModel(private val preferences: UserPreferences) : ViewModel() {

    val lastCityFlow: Flow<String?> = preferences.observeLastCityName()
    val temperatureUnit: Flow<String> = preferences.observeTemperatureUnit()
    val windUnit: Flow<String> = preferences.observeWindUnit()
    val recentCities: Flow<List<CityCoordinates>> = preferences.observeRecentCities()

    fun clearLastCity() {
        viewModelScope.launch {
            preferences.saveLastCityName("")
        }
    }

    fun setTemperatureUnit(unit: String) {
        viewModelScope.launch {
            preferences.setTemperatureUnit(unit)
        }
    }

    fun setWindUnit(unit: String) {
        viewModelScope.launch {
            preferences.setWindUnit(unit)
        }
    }

    fun clearRecentCities() {
        viewModelScope.launch {
            preferences.clearRecentCities()
        }
    }
}
