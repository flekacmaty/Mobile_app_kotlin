package com.example.mobile_app_project.data.repository.model

import kotlinx.serialization.Serializable

// Simple domain models used by Repository and UI/ViewModel layers

@Serializable
data class CityCoordinates(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class HourlyWeather(
    val dateTime: String,
    val temperature: Double
)

@Serializable
data class WeatherData(
    val currentTemp: Double,
    val currentWind: Double,
    val currentHumidity: Double?,
    val hourly: List<HourlyWeather>
)
