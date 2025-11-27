package com.example.mobile_app_project.ui.screens.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_app_project.data.repository.model.WeatherData
import com.example.mobile_app_project.ui.navigation.NavigationDestinations
import com.example.mobile_app_project.ui.theme.CloudWhite
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
    val json = remember { Json { ignoreUnknownKeys = true } }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Text(
            text = "Počasí",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = uiState.cityName,
            onValueChange = { viewModel.onCityNameChange(it) },
            label = { Text("Město") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.loadWeatherForCity(uiState.cityName) }) {
                Text("Vyhledat počasí")
            }
            Button(onClick = {
                val data: WeatherData? = uiState.weatherData
                if (data != null) {
                    val payload = json.encodeToString(data)
                    navController.navigate("${NavigationDestinations.DETAIL}?data=${payload}")
                } else {
                    navController.navigate(NavigationDestinations.DETAIL)
                }
            }) {
                Text("Detail")
            }
            Button(onClick = { viewModel.loadWeatherForCurrentLocation(context) }) {
                Text("Počasí zde")
            }
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
            Card(colors = CardDefaults.cardColors(containerColor = CloudWhite)) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = "Aktuální teplota: ${data.currentTemp} °C", color = TextDark)
                    Text(text = "Vítr: ${data.currentWind} m/s", color = TextSecondary)
                }
            }
        }
    }
}
