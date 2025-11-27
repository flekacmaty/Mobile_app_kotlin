package com.example.mobile_app_project.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResult(
    val id: Long? = null,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val timezone: String? = null
)

@Serializable
data class GeocodingResponse(
    val results: List<GeocodingResult>? = null
)
