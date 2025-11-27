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
            hourly = "temperature_2m",
            timezone = "auto"
        )
        val hourly = response.hourly
        if (hourly != null) {
            val hourlyList = mapHourly(hourly)
            // Current temp/wind nejsou v tomto zjednodušeném volání, nastavíme null/0
            Result.success(
                WeatherData(
                    currentTemp = hourly.temperature2m.firstOrNull() ?: 0.0,
                    currentWind = 0.0,
                    currentHumidity = null,
                    hourly = hourlyList
                )
            )
        } else {
            Result.failure(IllegalStateException("Nepodařilo se získat předpověď"))
        }
    } catch (t: Throwable) {
        Result.failure(t)
    }

    fun mapHourly(hourly: com.example.mobile_app_project.data.remote.dto.Hourly): List<HourlyWeather> {
        val times = hourly.time
        val temps = hourly.temperature2m
        val size = minOf(times.size, temps.size)
        return (0 until size).map { idx ->
            HourlyWeather(dateTime = times[idx], temperature = temps[idx])
        }
    }
}
