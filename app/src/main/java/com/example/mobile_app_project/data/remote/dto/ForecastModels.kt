package com.example.mobile_app_project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeather(
    val time: String,
    @SerialName("temperature_2m") val temperature2m: Double? = null,
    @SerialName("wind_speed_10m") val windSpeed10m: Double? = null
)

@Serializable
data class HourlyWeather(
    val time: List<String> = emptyList(),
    @SerialName("temperature_2m") val temperature2m: List<Double> = emptyList(),
    @SerialName("relative_humidity_2m") val relativeHumidity2m: List<Double> = emptyList(),
    @SerialName("wind_speed_10m") val windSpeed10m: List<Double> = emptyList()
)

@Serializable
data class ForecastResponse(
    @SerialName("current") val current: CurrentWeather? = null,
    @SerialName("hourly") val hourly: HourlyWeather? = null
)

