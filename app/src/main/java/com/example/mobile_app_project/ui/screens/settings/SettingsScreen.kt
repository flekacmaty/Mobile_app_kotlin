package com.example.mobile_app_project.ui.screens.settings

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_app_project.data.repository.model.CityCoordinates
import com.example.mobile_app_project.ui.theme.CardStroke
import com.example.mobile_app_project.ui.theme.CloudWhite
import com.example.mobile_app_project.ui.theme.SkyBlueLight
import com.example.mobile_app_project.ui.theme.TextDark
import com.example.mobile_app_project.ui.theme.TextSecondary
import com.example.mobile_app_project.viewmodel.SettingsViewModel
import com.example.mobile_app_project.viewmodel.WeatherViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    weatherViewModel: WeatherViewModel
) {
    val tempUnit by settingsViewModel.temperatureUnit.collectAsState(initial = "C")
    val windUnit by settingsViewModel.windUnit.collectAsState(initial = "m_s")
    val recentCities by settingsViewModel.recentCities.collectAsState(initial = emptyList())

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
            Text("Nastavení", style = MaterialTheme.typography.titleLarge, color = TextDark)
            Text("Jednotky teploty", style = MaterialTheme.typography.titleMedium, color = TextDark)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RowWithRadio("Celsius (°C)", tempUnit == "C") { settingsViewModel.setTemperatureUnit("C") }
                RowWithRadio("Fahrenheit (°F)", tempUnit == "F") { settingsViewModel.setTemperatureUnit("F") }
            }
            Text("Jednotky větru", style = MaterialTheme.typography.titleMedium, color = TextDark)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RowWithRadio("m/s", windUnit == "m_s") { settingsViewModel.setWindUnit("m_s") }
                RowWithRadio("km/h", windUnit == "km_h") { settingsViewModel.setWindUnit("km_h") }
            }
            HorizontalDivider()
            Text("Hledané (posledních 20)", style = MaterialTheme.typography.titleMedium, color = TextDark)
            if (recentCities.isEmpty()) {
                Text("Žádné položky", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(recentCities) { city: CityCoordinates ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CloudWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val lat = city.latitude
                                    val lon = city.longitude
                                    val encodedName = Uri.encode(city.name)
                                    if (!lat.isNaN() && !lon.isNaN()) {
                                        weatherViewModel.loadForecastForCoordinates(lat, lon, city.name)
                                        navController.navigate("detail?cityName=$encodedName&lat=$lat&lon=$lon")
                                    } else {
                                        weatherViewModel.loadWeatherForCityDetail(city.name)
                                        navController.navigate("detail?cityName=$encodedName&lat=&lon=")
                                    }
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(city.name, style = MaterialTheme.typography.titleMedium, color = TextDark)
                                Text("${city.latitude}, ${city.longitude}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { settingsViewModel.clearRecentCities() }) { Text("Smazat historii hledání", color = TextDark) }
        }
    }
}

@Composable
private fun RowWithRadio(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(label, color = TextDark)
    }
}
