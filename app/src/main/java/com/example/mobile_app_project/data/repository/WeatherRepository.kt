package com.example.mobile_app_project.data.repository

import com.example.mobile_app_project.data.remote.OpenMeteoClient
import com.example.mobile_app_project.data.remote.dto.ForecastResponse
import com.example.mobile_app_project.data.remote.dto.GeocodingResponse
import com.example.mobile_app_project.data.repository.model.CityCoordinates
import com.example.mobile_app_project.data.repository.model.HourlyWeather
import com.example.mobile_app_project.data.repository.model.WeatherData

/**
 * Repository that coordinates between remote API and maps DTOs to domain models.
 */
class WeatherRepository {

    private val geocodingService = OpenMeteoClient.geocoding
    private val forecastService = OpenMeteoClient.forecast

    suspend fun searchCityByName(name: String): Result<CityCoordinates> = try {
        val response: GeocodingResponse = geocodingService.searchCity(name = name, count = 1)
        val first = response.results?.firstOrNull()
        if (first != null) {
            Result.success(
                CityCoordinates(
                    name = first.name,
                    latitude = first.latitude,
                    longitude = first.longitude
                )
            )
        } else {
            Result.failure(IllegalStateException("City not found"))
        }
    } catch (t: Throwable) {
        Result.failure(t)
    }

    suspend fun getForecastForCoordinates(lat: Double, lon: Double): Result<WeatherData> = try {
        val response: ForecastResponse = forecastService.getForecast(
            latitude = lat,
            longitude = lon,
            current = "temperature_2m,wind_speed_10m",
            hourly = "time,temperature_2m,relative_humidity_2m,wind_speed_10m",
            timezone = "auto"
        )
        val current = response.current
        val hourly = response.hourly
        if (current != null && hourly != null) {
            val hourlyList = mapHourly(hourly)
            Result.success(
                WeatherData(
                    currentTemp = current.temperature2m ?: 0.0,
                    currentWind = current.windSpeed10m ?: 0.0,
                    currentHumidity = null, // Open-Meteo current may not include humidity in selected vars
                    hourly = hourlyList
                )
            )
        } else {
            Result.failure(IllegalStateException("Incomplete forecast data"))
        }
    } catch (t: Throwable) {
        Result.failure(t)
    }

    private fun mapHourly(hourly: com.example.mobile_app_project.data.remote.dto.HourlyWeather): List<HourlyWeather> {
        val times = hourly.time
        val temps = hourly.temperature2m
        val hums = hourly.relativeHumidity2m
        val winds = hourly.windSpeed10m
        val size = listOf(times.size, temps.size, hums.size, winds.size).minOrNull() ?: 0
        return (0 until size).map { idx ->
            HourlyWeather(
                time = times[idx],
                temperature = temps[idx],
                humidity = hums.getOrNull(idx),
                windSpeed = winds[idx]
            )
        }
    }
}
