package com.example.mobile_app_project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResult(
    val name: String,
    val country: String? = null,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class GeocodingResponse(
    val results: List<GeocodingResult>? = null
)
