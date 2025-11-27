package com.example.mobile_app_project.ui.screens.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_app_project.data.repository.WeatherRepository
import com.example.mobile_app_project.data.repository.model.CityCoordinates
import com.example.mobile_app_project.viewmodel.SettingsViewModel
import com.example.mobile_app_project.ui.navigation.NavigationDestinations
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun FavoritesScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    weatherViewModel: com.example.mobile_app_project.viewmodel.WeatherViewModel
) {
    val favoritesJson by settingsViewModel.lastCityFlow.collectAsState(initial = null) // placeholder; replace with observeFavorites
    val json = Json { ignoreUnknownKeys = true }

    val favorites: List<CityCoordinates> = favoritesJson?.let {
        runCatching { json.decodeFromString<List<CityCoordinates>>(it) }.getOrNull()
    } ?: emptyList()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (favorites.isEmpty()) {
            Text("Žádná oblíbená města")
        } else {
            LazyColumn {
                items(favorites) { city ->
                    Text(text = city.name, modifier = Modifier.padding(vertical = 8.dp))
                    // On click navigate to detail with params
                    // navController.navigate("${NavigationDestinations.DETAIL}?cityName=${city.name}&lat=${city.latitude}&lon=${city.longitude}")
                }
            }
        }
    }
}

