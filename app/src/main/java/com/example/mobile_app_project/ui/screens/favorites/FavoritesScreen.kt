package com.example.mobile_app_project.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_app_project.data.local.UserPreferences
import com.example.mobile_app_project.data.repository.model.CityCoordinates
import com.example.mobile_app_project.ui.navigation.NavigationDestinations
import com.example.mobile_app_project.ui.theme.CardStroke
import com.example.mobile_app_project.ui.theme.CloudWhite
import com.example.mobile_app_project.ui.theme.SkyBlueLight
import com.example.mobile_app_project.ui.theme.TextDark
import com.example.mobile_app_project.ui.theme.TextSecondary
import com.example.mobile_app_project.viewmodel.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Composable
fun FavoritesScreen(
    navController: NavController,
    weatherViewModel: WeatherViewModel,
    preferences: UserPreferences
) {
    val favorites by preferences.observeFavoritesList().collectAsState(initial = emptyList())
    val scope = CoroutineScope(Dispatchers.IO)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CloudWhite, SkyBlueLight)))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Oblíbená města", style = androidx.compose.material3.MaterialTheme.typography.titleLarge, color = TextDark)
        if (favorites.isEmpty()) {
            Text("Žádná oblíbená města", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favorites) { city: CityCoordinates ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CloudWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardStroke),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("${NavigationDestinations.DETAIL}?cityName=${URLEncoder.encode(city.name, "UTF-8")}&lat=${city.latitude}&lon=${city.longitude}")
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(city.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = TextDark)
                                Text(
                                    text = "${city.latitude}, ${city.longitude}",
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                            IconButton(onClick = {
                                scope.launch { preferences.removeFavorite(city.name) }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Odebrat z oblíbených",
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
