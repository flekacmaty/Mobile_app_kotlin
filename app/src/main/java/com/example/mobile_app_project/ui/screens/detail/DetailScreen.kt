package com.example.mobile_app_project.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobile_app_project.data.repository.model.HourlyWeather
import com.example.mobile_app_project.data.repository.model.WeatherData
import com.example.mobile_app_project.ui.theme.CloudWhite
import com.example.mobile_app_project.ui.theme.TextDark
import com.example.mobile_app_project.ui.theme.TextSecondary
import kotlinx.serialization.json.Json

@Composable
fun DetailScreen(jsonData: String = "") {
    val json = remember { Json { ignoreUnknownKeys = true } }
    val weatherData: WeatherData? = remember(jsonData) {
        if (jsonData.isNotBlank()) runCatching { json.decodeFromString<WeatherData>(jsonData) }.getOrNull() else null
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (weatherData != null) {
            Text(text = "Detail počasí", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
            Card(colors = CardDefaults.cardColors(containerColor = CloudWhite), modifier = Modifier.padding(top = 8.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = "Teplota: ${weatherData.currentTemp} °C", color = TextDark, fontWeight = FontWeight.Medium)
                    Text(text = "Vítr: ${weatherData.currentWind} m/s", color = TextSecondary)
                    Text(text = "Vlhkost: ${weatherData.currentHumidity ?: 0.0} %", color = TextSecondary)
                }
            }
            Text(text = "Hodinová předpověď:", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            LazyColumn {
                items(weatherData.hourly) { hour: HourlyWeather ->
                    Text(text = "${hour.time} – ${hour.temperature} °C, vítr ${hour.windSpeed} m/s${hour.humidity?.let { ", vlhkost ${it} %" } ?: ""}", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        } else {
            Text(text = "Bez dat pro detail")
        }
    }
}
