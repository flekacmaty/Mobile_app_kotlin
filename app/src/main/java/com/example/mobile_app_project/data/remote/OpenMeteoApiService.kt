package com.example.mobile_app_project.data.remote

import com.example.mobile_app_project.data.remote.dto.ForecastResponse
import com.example.mobile_app_project.data.remote.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApiService {

    // Geocoding API: https://geocoding-api.open-meteo.com/v1/search
    @GET("/v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 1
    ): GeocodingResponse

    // Forecast API: https://api.open-meteo.com/v1/forecast
    @GET("/v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        // Request both current and hourly variables per docs
        @Query("current") current: String = "temperature_2m,wind_speed_10m",
        @Query("hourly") hourly: String = "temperature_2m",
        @Query("timezone") timezone: String = "auto"
    ): ForecastResponse
}
