package com.example.mobile_app_project.ui.screens.settings

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_app_project.data.repository.model.CityCoordinates
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

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nastavení", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Text("Jednotky teploty")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RowWithRadio(label = "Celsius (°C)", selected = tempUnit == "C") { settingsViewModel.setTemperatureUnit("C") }
            RowWithRadio(label = "Fahrenheit (°F)", selected = tempUnit == "F") { settingsViewModel.setTemperatureUnit("F") }
        }
        Text("Jednotky větru")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RowWithRadio(label = "m/s", selected = windUnit == "m_s") { settingsViewModel.setWindUnit("m_s") }
            RowWithRadio(label = "km/h", selected = windUnit == "km_h") { settingsViewModel.setWindUnit("km_h") }
        }
        HorizontalDivider()
        Text("Hledané (posledních 20)")
        if (recentCities.isEmpty()) {
            Text("Žádné položky")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(recentCities) { city: CityCoordinates ->
                    Text(
                        text = city.name,
                        modifier = Modifier.clickable {
                            val lat = city.latitude
                            val lon = city.longitude
                            if (!lat.isNaN() && !lon.isNaN()) {
                                weatherViewModel.loadForecastForCoordinates(lat, lon, city.name)
                                navController.navigate("detail?cityName=${Uri.encode(city.name)}&lat=$lat&lon=$lon")
                            } else {
                                weatherViewModel.loadWeatherForCityDetail(city.name)
                                navController.navigate("detail?cityName=${Uri.encode(city.name)}&lat=&lon=")
                            }
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { settingsViewModel.clearRecentCities() }) { Text("Smazat historii hledání") }
    }
}

@Composable
private fun RowWithRadio(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(label)
    }
}
