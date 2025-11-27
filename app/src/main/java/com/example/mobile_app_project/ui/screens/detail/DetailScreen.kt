package com.example.mobile_app_project.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobile_app_project.data.repository.model.HourlyWeather
import com.example.mobile_app_project.data.repository.model.WeatherData
import com.example.mobile_app_project.ui.theme.CloudWhite
import com.example.mobile_app_project.ui.theme.TextDark
import com.example.mobile_app_project.ui.theme.TextSecondary
import com.example.mobile_app_project.viewmodel.WeatherViewModel
import kotlinx.serialization.json.Json

@Composable
fun DetailScreen(cityName: String = "", lat: Double? = null, lon: Double? = null, viewModel: WeatherViewModel? = null, jsonData: String = "") {
    val json = remember { Json { ignoreUnknownKeys = true } }
    val initialData: WeatherData? = remember(jsonData) {
        if (jsonData.isNotBlank()) runCatching { json.decodeFromString<WeatherData>(jsonData) }.getOrNull() else null
    }

    LaunchedEffect(key1 = cityName, key2 = lat, key3 = lon) {
        if (viewModel != null && lat != null && lon != null) {
            viewModel.onCityNameChange(cityName)
            // přímo načteme forecast pro dané souřadnice
            viewModel.loadWeatherForCity(cityName)
        }
    }

    val weatherData = initialData ?: viewModel?.uiState?.collectAsState()?.value?.weatherData

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (weatherData != null) {
            Text(text = cityName.ifBlank { "Detail počasí" }, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
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
                    Text(text = "${hour.dateTime} – ${hour.temperature} °C", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        } else {
            Text(text = "Bez dat pro detail")
        }
    }
}
