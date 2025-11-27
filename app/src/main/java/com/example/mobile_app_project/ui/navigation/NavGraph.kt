package com.example.mobile_app_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile_app_project.ui.screens.detail.DetailScreen
import com.example.mobile_app_project.ui.screens.home.HomeScreen
import com.example.mobile_app_project.ui.screens.settings.SettingsScreen
import com.example.mobile_app_project.data.local.UserPreferences
import com.example.mobile_app_project.data.repository.WeatherRepository
import com.example.mobile_app_project.viewmodel.WeatherViewModel
import androidx.compose.ui.platform.LocalContext

/**
 * Navigation destinations and placeholder for future NavHost.
 */
object NavigationDestinations {
    const val HOME = "home"
    const val DETAIL = "detail"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = remember { WeatherViewModel(WeatherRepository(), UserPreferences(context)) }

    NavHost(navController = navController, startDestination = NavigationDestinations.HOME) {
        composable(NavigationDestinations.HOME) { HomeScreen(navController, viewModel) }
        composable(NavigationDestinations.DETAIL) { DetailScreen() }
        composable(NavigationDestinations.SETTINGS) { SettingsScreen() }
    }
}
