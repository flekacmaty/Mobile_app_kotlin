package com.example.mobile_app_project.data.remote

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object OpenMeteoClient {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    // Base URL will be set per service creation
    private fun retrofit(baseUrl: String): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    val geocoding: OpenMeteoApiService by lazy {
        retrofit("https://geocoding-api.open-meteo.com/").create(OpenMeteoApiService::class.java)
    }

    val forecast: OpenMeteoApiService by lazy {
        retrofit("https://api.open-meteo.com/").create(OpenMeteoApiService::class.java)
    }
}
