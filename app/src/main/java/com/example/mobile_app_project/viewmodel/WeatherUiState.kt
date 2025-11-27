package com.example.mobile_app_project.viewmodel

import com.example.mobile_app_project.data.repository.model.WeatherData

data class WeatherUiState(
    val cityName: String = "",
    val isLoading: Boolean = false,
    val weatherData: WeatherData? = null,
    val errorMessage: String? = null
)

