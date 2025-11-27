package com.example.mobile_app_project.ui.screens.favorites

import android.net.Uri
import androidx.compose.foundation.clickable
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
import com.example.mobile_app_project.data.local.UserPreferences
import com.example.mobile_app_project.data.repository.model.CityCoordinates
import com.example.mobile_app_project.ui.navigation.NavigationDestinations
import com.example.mobile_app_project.viewmodel.WeatherViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    weatherViewModel: WeatherViewModel,
    preferences: UserPreferences
) {
    val favorites by preferences.observeFavoritesList().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Oblíbená města", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        if (favorites.isEmpty()) {
            Text("Žádná oblíbená města")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(favorites) { city: CityCoordinates ->
                    Text(
                        text = city.name,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(
                                    "${NavigationDestinations.DETAIL}?cityName=${Uri.encode(city.name)}&lat=${city.latitude}&lon=${city.longitude}".trim()
                                )
                            }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}
