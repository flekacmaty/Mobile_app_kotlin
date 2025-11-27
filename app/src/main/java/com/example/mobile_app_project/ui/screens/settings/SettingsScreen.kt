package com.example.mobile_app_project.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_app_project.data.local.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: com.example.mobile_app_project.viewmodel.SettingsViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = UserPreferences(context)
    val scope = CoroutineScope(Dispatchers.IO)

    val tempUnit by prefs.observeTemperatureUnit().collectAsState(initial = "C")
    val windUnit by prefs.observeWindUnit().collectAsState(initial = "m_s")
    val recentCities by prefs.observeRecentCities().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nastavení", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Text("Jednotky teploty")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RowWithRadio(label = "Celsius (°C)", selected = tempUnit == "C") { scope.launch { prefs.setTemperatureUnit("C") } }
            RowWithRadio(label = "Fahrenheit (°F)", selected = tempUnit == "F") { scope.launch { prefs.setTemperatureUnit("F") } }
        }
        Text("Jednotky větru")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RowWithRadio(label = "m/s", selected = windUnit == "m_s") { scope.launch { prefs.setWindUnit("m_s") } }
            RowWithRadio(label = "km/h", selected = windUnit == "km_h") { scope.launch { prefs.setWindUnit("km_h") } }
        }
        Divider()
        Text("Hledané (posledních 20)")
        if (recentCities.isEmpty()) {
            Text("Žádné položky")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(recentCities) { name ->
                    Text(text = name)
                }
            }
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Button(onClick = { scope.launch { prefs.clearRecentCities() } }) { Text("Smazat historii hledání") }
    }
}

@Composable
private fun RowWithRadio(label: String, selected: Boolean, onSelect: () -> Unit) {
    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RadioButton(selected = selected, onClick = { onSelect() })
        Text(label)
    }
}
