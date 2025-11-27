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
    val time: String,
    val temperature: Double,
    val humidity: Double?,
    val windSpeed: Double?
)

@Serializable
data class WeatherData(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val currentTemperature: Double?,
    val currentWindSpeed: Double?,
    val currentHumidity: Double?,
    val hourly: List<HourlyWeather>
)
