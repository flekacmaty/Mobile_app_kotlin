package com.example.mobile_app_project.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Hourly(
    val time: List<String> = emptyList(),
    @SerialName("temperature_2m") val temperature2m: List<Double> = emptyList()
)

@Serializable
data class ForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    @SerialName("utc_offset_seconds") val utcOffsetSeconds: Int,
    val hourly: Hourly? = null
)
