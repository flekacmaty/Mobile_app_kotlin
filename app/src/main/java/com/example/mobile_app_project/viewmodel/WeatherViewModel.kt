package com.example.mobile_app_project.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
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

    // Remove auto-load of last city to keep Home showing only current location weather
    // init { /* intentionally disabled */ }

    fun onCityNameChange(name: String) {
        _uiState.value = _uiState.value.copy(cityName = name, errorMessage = null)
    }

    // Keep method for loading by city if needed elsewhere (e.g., Detail)
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
        val forecastResult = repository.getWeatherForCoordinates(city.latitude, city.longitude, city.name)
        forecastResult.fold(onSuccess = { data: WeatherData ->
            // Do not override Home's current location weather if search triggered
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
        }, onFailure = { err ->
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "Unknown error")
        })
    }

    fun loadWeatherForCurrentLocation(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val coords = getCurrentCoordinates(context)
            if (coords != null) {
                val (lat, lon) = coords
                repository.getWeatherForCoordinates(lat, lon).fold(onSuccess = { data ->
                    _uiState.value = _uiState.value.copy(isLoading = false, weatherData = data, errorMessage = null)
                }, onFailure = { err ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "Unknown error")
                })
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Nelze získat aktuální polohu (zkontrolujte oprávnění)")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentCoordinates(context: Context): Pair<Double, Double>? {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted && !coarseGranted) return null

        val fused = LocationServices.getFusedLocationProviderClient(context)
        val last = suspendCancellableCoroutine<Location?> { cont ->
            fused.lastLocation.addOnSuccessListener { loc ->
                if (!cont.isCancelled) cont.resume(loc)
            }.addOnFailureListener { _ ->
                if (!cont.isCancelled) cont.resume(null)
            }
        }
        val loc = last ?: suspendCancellableCoroutine<Location?> { cont ->
            fused.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { l -> if (!cont.isCancelled) cont.resume(l) }
                .addOnFailureListener { _ -> if (!cont.isCancelled) cont.resume(null) }
        }
        return loc?.let { it.latitude to it.longitude }
    }

    fun searchAndNavigate(name: String, navController: androidx.navigation.NavController) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.getWeatherForCityName(name).fold(onSuccess = { data: WeatherData ->
                // Add to recent searches
                preferences.addRecentCity(data.cityName)
                // Do not set weatherData to keep Home pinned to current location
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = null)
                navController.navigate("detail?cityName=${data.cityName}&lat=${data.latitude}&lon=${data.longitude}")
            }, onFailure = { err ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "City not found")
            })
        }
    }

    // Explicitly load forecast for given coordinates (used by Detail)
    fun loadForecastForCoordinates(lat: Double, lon: Double, cityNameHint: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.getWeatherForCoordinates(lat, lon, cityNameHint ?: "")
                .fold(onSuccess = { data: WeatherData ->
                    _uiState.value = _uiState.value.copy(isLoading = false, weatherData = data, errorMessage = null)
                }, onFailure = { err ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "Unknown error")
                })
        }
    }

    // Load weather by city for Detail (sets weatherData)
    fun loadWeatherForCityDetail(name: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.searchCityByName(name).fold(
                onSuccess = { city: com.example.mobile_app_project.data.repository.model.CityCoordinates ->
                    repository.getWeatherForCoordinates(city.latitude, city.longitude, city.name).fold(
                        onSuccess = { data ->
                            _uiState.value = _uiState.value.copy(isLoading = false, weatherData = data, errorMessage = null)
                        },
                        onFailure = { err ->
                            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "Unknown error")
                        }
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "Unknown error")
                }
            )
        }
    }
}
