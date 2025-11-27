package com.example.mobile_app_project.data.repository

import com.example.mobile_app_project.data.remote.OpenMeteoClient
import com.example.mobile_app_project.data.remote.dto.ForecastResponse
import com.example.mobile_app_project.data.remote.dto.GeocodingResponse
import com.example.mobile_app_project.data.remote.dto.HourlyBlock
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
            ?: return Result.failure(IllegalArgumentException("City not found"))
        Result.success(CityCoordinates(first.name, first.latitude, first.longitude))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getWeatherForCityName(cityName: String): Result<WeatherData> = try {
        val coords = searchCityByName(cityName).getOrElse { return Result.failure(it) }
        val forecast = forecastService.getForecast(latitude = coords.latitude, longitude = coords.longitude)
        Result.success(forecast.toWeatherData(coords.name))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getWeatherForCoordinates(lat: Double, lon: Double, cityNameHint: String = "Current location"): Result<WeatherData> = try {
        val forecast = forecastService.getForecast(latitude = lat, longitude = lon)
        Result.success(forecast.toWeatherData(cityNameHint))
    } catch (e: Exception) { Result.failure(e) }
}

// Mapping extension – safe, no !!, defensive against null blocks and length mismatch
fun ForecastResponse.toWeatherData(cityName: String): WeatherData {
    val currentBlock = current
    val hourlyBlock: HourlyBlock? = hourly

    val times = hourlyBlock?.time ?: emptyList()
    val temps = hourlyBlock?.temperature2m ?: emptyList()
    val hums = hourlyBlock?.relativeHumidity2m ?: emptyList()
    val winds = hourlyBlock?.windSpeed10m ?: emptyList()

    val size = listOf(times.size, temps.size).minOrNull() ?: 0

    val hourlyList = (0 until size).map { i ->
        HourlyWeather(
            time = times[i],
            temperature = temps[i],
            humidity = hums.getOrNull(i),
            windSpeed = winds.getOrNull(i)
        )
    }

    return WeatherData(
        cityName = cityName,
        currentTemperature = currentBlock?.temperature2m,
        currentWindSpeed = currentBlock?.windSpeed10m,
        currentHumidity = currentBlock?.relativeHumidity2m,
        hourly = hourlyList
    )
}
