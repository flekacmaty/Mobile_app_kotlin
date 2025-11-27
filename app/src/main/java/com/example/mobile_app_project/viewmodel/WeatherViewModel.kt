package com.example.mobile_app_project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_app_project.data.local.UserPreferences
import com.example.mobile_app_project.data.repository.WeatherRepository
import com.example.mobile_app_project.data.repository.model.CityCoordinates
import com.example.mobile_app_project.data.repository.model.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        // Load last city from DataStore on startup
        viewModelScope.launch {
            preferences.observeLastCityName()
                .filterNotNull()
                .collect { lastCity ->
                    if (lastCity.isNotBlank()) {
                        _uiState.value = _uiState.value.copy(cityName = lastCity)
                        loadWeatherForCity(lastCity)
                    }
                }
        }
    }

    fun onCityNameChange(name: String) {
        _uiState.value = _uiState.value.copy(cityName = name, errorMessage = null)
    }

    fun loadWeatherForCity(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            repository.searchCityByName(name).fold(
                onSuccess = { city: CityCoordinates ->
                    fetchForecastAndPersist(city)
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "Unknown error")
                }
            )
        }
    }

    private suspend fun fetchForecastAndPersist(city: CityCoordinates) {
        preferences.saveLastCityName(city.name)
        val forecastResult = repository.getForecastForCoordinates(city.latitude, city.longitude)
        forecastResult.fold(onSuccess = { data: WeatherData ->
            _uiState.value = _uiState.value.copy(isLoading = false, weatherData = data, errorMessage = null)
        }, onFailure = { err ->
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "Unknown error")
        })
    }
}
