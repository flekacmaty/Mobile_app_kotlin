package com.example.mobile_app_project.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobile_app_project.data.repository.model.CityCoordinates
import com.example.mobile_app_project.data.repository.model.HourlyWeather
import com.example.mobile_app_project.data.repository.model.WeatherData
import com.example.mobile_app_project.ui.theme.CloudWhite
import com.example.mobile_app_project.ui.theme.TextDark
import com.example.mobile_app_project.ui.theme.TextSecondary
import com.example.mobile_app_project.viewmodel.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import com.example.mobile_app_project.data.local.UserPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star

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
    val context = androidx.compose.ui.platform.LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = remember { CoroutineScope(Dispatchers.IO) }
    val favorites by preferences.observeFavoritesList().collectAsState(initial = emptyList())
    val effectiveCityName = cityName.ifBlank { viewModel?.uiState?.collectAsState()?.value?.cityName ?: cityName }
    val isFavorite = favorites.any { it.name.equals(effectiveCityName, ignoreCase = true) }

    // Helper: group next 3 days
    fun groupHourlyNext3Days(list: List<HourlyWeather>): List<Pair<String, List<HourlyWeather>>> {
        val dateMap = LinkedHashMap<String, MutableList<HourlyWeather>>()
        list.forEach { hw ->
            val dateKey = hw.time.substringBefore('T') // e.g. 2025-11-27
            dateMap.getOrPut(dateKey) { mutableListOf() }.add(hw)
        }
        return dateMap.entries.take(3).map { it.key to it.value }
    }

    val grouped = remember(weatherData) { weatherData?.let { groupHourlyNext3Days(it.hourly) } ?: emptyList() }

    // Hoist unit prefs to composable scope (not inside LazyColumn builder)
    val prefs = UserPreferences(context)
    val tempUnit by prefs.observeTemperatureUnit().collectAsState(initial = "C")
    val windUnit by prefs.observeWindUnit().collectAsState(initial = "m_s")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (weatherData != null) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = effectiveCityName.ifBlank { "Detail počasí" }, style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
                IconButton(onClick = {
                    scope.launch {
                        if (isFavorite) preferences.removeFavorite(effectiveCityName) else preferences.addFavorite(
                            CityCoordinates(effectiveCityName, lat ?: weatherData.latitude, lon ?: weatherData.longitude)
                        )
                    }
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (isFavorite) "Odebrat z oblíbených" else "Přidat do oblíbených",
                        tint = if (isFavorite) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = CloudWhite), modifier = Modifier.padding(top = 8.dp)) {
                val prefs = UserPreferences(context)
                val displayTemp = if (tempUnit == "F") (weatherData.currentTemperature ?: 0.0) * 9 / 5 + 32 else (weatherData.currentTemperature ?: 0.0)
                val displayWind = if (windUnit == "km_h") ((weatherData.currentWindSpeed ?: 0.0) * 3.6) else (weatherData.currentWindSpeed ?: 0.0)
                val windLabel = if (windUnit == "km_h") "km/h" else "m/s"
                val tempLabel = if (tempUnit == "F") "°F" else "°C"
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = "Teplota: ${String.format("%.1f", displayTemp)} $tempLabel", color = TextDark, fontWeight = FontWeight.Medium)
                    Text(text = "Vítr: ${String.format("%.1f", displayWind)} $windLabel", color = TextSecondary)
                    Text(text = "Vlhkost: ${weatherData.currentHumidity ?: 0.0} %", color = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Předpověď na 3 dny", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                grouped.forEachIndexed { dayIndex, pair ->
                    val (dateKey, itemsForDay) = pair
                    item { Text(text = "Den ${dayIndex + 1}", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp)) }
                    itemsIndexed(itemsForDay) { _, hour: HourlyWeather ->
                        val timeLabel = hour.time.substringAfter('T').take(5) // HH:MM
                        val displayTemp = if (tempUnit == "F") (hour.temperature) * 9 / 5 + 32 else hour.temperature
                        val tempLabel = if (tempUnit == "F") "°F" else "°C"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(timeLabel, color = TextDark)
                            Text("${String.format("%.1f", displayTemp)} $tempLabel", color = TextSecondary)
                        }
                    }
                }
            }
        } else {
            Text(text = "Bez dat pro detail")
        }
    }
}
