package com.example.mobile_app_project.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_app_project.data.local.UserPreferences
import com.example.mobile_app_project.data.repository.model.WeatherData
import com.example.mobile_app_project.ui.navigation.NavigationDestinations
import com.example.mobile_app_project.ui.theme.CloudWhite
import com.example.mobile_app_project.ui.theme.SkyBlueLight
import com.example.mobile_app_project.ui.theme.TextDark
import com.example.mobile_app_project.ui.theme.TextSecondary
import com.example.mobile_app_project.viewmodel.WeatherViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WeatherViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Auto-load current location weather on first composition
    var autoLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = uiState.weatherData) {
        if (!autoLoaded && uiState.weatherData == null) {
            viewModel.loadWeatherForCurrentLocation(context)
            autoLoaded = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CloudWhite, SkyBlueLight)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Aktuální počasí",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )

            // Search input stays only for navigation to detail, not affecting Home data
            OutlinedTextField(
                value = uiState.cityName,
                onValueChange = { viewModel.onCityNameChange(it) },
                label = { Text("Město") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val name = uiState.cityName.trim()
                    if (name.isNotEmpty()) {
                        viewModel.searchAndNavigate(name, navController)
                    }
                }) { Text("Vyhledat počasí") }
                Button(onClick = { viewModel.loadWeatherForCurrentLocation(context) }) { Text("Počasí zde") }
            }

            if (uiState.isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Načítám…")
                }
            }

            uiState.errorMessage?.let { err ->
                Text(text = "Chyba: $err", color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }

            uiState.weatherData?.let { data ->
                val prefs = UserPreferences(context)
                val tempUnit by prefs.observeTemperatureUnit().collectAsState(initial = "C")
                val windUnit by prefs.observeWindUnit().collectAsState(initial = "m_s")

                val displayTemp = if (tempUnit == "F") (data.currentTemperature ?: 0.0) * 9 / 5 + 32 else (data.currentTemperature ?: 0.0)
                val displayWind = if (windUnit == "km_h") ((data.currentWindSpeed ?: 0.0) * 3.6) else (data.currentWindSpeed ?: 0.0)
                val windLabel = if (windUnit == "km_h") "km/h" else "m/s"
                val tempLabel = if (tempUnit == "F") "°F" else "°C"

                Card(colors = CardDefaults.cardColors(containerColor = CloudWhite)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Teplota: ${String.format("%.1f", displayTemp)} $tempLabel", color = TextDark)
                        Text(text = "Vítr: ${String.format("%.1f", displayWind)} $windLabel", color = TextSecondary)
                    }
                }
            }
        }
    }
}
