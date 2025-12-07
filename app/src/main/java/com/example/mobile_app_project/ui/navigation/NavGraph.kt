package com.example.mobile_app_project.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.mobile_app_project.ui.screens.detail.DetailScreen
import com.example.mobile_app_project.ui.screens.home.HomeScreen
import com.example.mobile_app_project.ui.screens.settings.SettingsScreen
import com.example.mobile_app_project.ui.screens.favorites.FavoritesScreen
import com.example.mobile_app_project.data.local.UserPreferences
import com.example.mobile_app_project.data.repository.WeatherRepository
import com.example.mobile_app_project.viewmodel.SettingsViewModel
import com.example.mobile_app_project.viewmodel.WeatherViewModel

/**
 * Navigation destinations and placeholder for future NavHost.
 */
object NavigationDestinations {
    const val HOME = "home"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val DETAIL = "detail"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val weatherRepository = remember { WeatherRepository() }
    val weatherViewModel = remember { WeatherViewModel(weatherRepository, userPreferences) }
    val settingsViewModel = remember { SettingsViewModel(userPreferences) }

    val items = listOf(NavigationDestinations.HOME, NavigationDestinations.FAVORITES, NavigationDestinations.SETTINGS)
    val backStack = navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            val currentRoute = backStack.value?.destination?.route ?: NavigationDestinations.HOME
            NavigationBar {
                items.forEach { route ->
                    val selected = currentRoute.startsWith(route)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(NavigationDestinations.HOME) { inclusive = false }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            when (route) {
                                NavigationDestinations.HOME -> Icon(Icons.Filled.Home, contentDescription = "Home")
                                NavigationDestinations.FAVORITES -> Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
                                NavigationDestinations.SETTINGS -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                                else -> Icon(Icons.Filled.Home, contentDescription = null)
                            }
                        },
                        label = { Text(
                            when (route) {
                                NavigationDestinations.HOME -> "Domů"
                                NavigationDestinations.FAVORITES -> "Oblíbené"
                                NavigationDestinations.SETTINGS -> "Nastavení"
                                else -> route
                            }
                        ) }
                    )
                }
            }
        }
    ) { innerPadding: PaddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(navController = navController, startDestination = NavigationDestinations.HOME) {
                composable(NavigationDestinations.HOME) { HomeScreen(navController, weatherViewModel) }
                composable(NavigationDestinations.FAVORITES) {
                    FavoritesScreen(navController, weatherViewModel, userPreferences)
                }
                composable(
                    route = NavigationDestinations.DETAIL + "?cityName={cityName}&lat={lat}&lon={lon}",
                    arguments = listOf(
                        navArgument("cityName") { type = NavType.StringType; defaultValue = "" },
                        navArgument("lat") { type = NavType.StringType; defaultValue = "" },
                        navArgument("lon") { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStackEntry ->
                    val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
                    val latStr = backStackEntry.arguments?.getString("lat") ?: ""
                    val lonStr = backStackEntry.arguments?.getString("lon") ?: ""
                    DetailScreen(cityName, latStr.toDoubleOrNull(), lonStr.toDoubleOrNull(), weatherViewModel, "")
                }
                composable(NavigationDestinations.SETTINGS) {
                    SettingsScreen(navController, settingsViewModel, weatherViewModel)
                }
            }
        }
    }
}
