package com.example.mobile_app_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.mobile_app_project.ui.screens.detail.DetailScreen
import com.example.mobile_app_project.ui.screens.home.HomeScreen
import com.example.mobile_app_project.ui.screens.settings.SettingsScreen
import com.example.mobile_app_project.data.local.UserPreferences
import com.example.mobile_app_project.data.repository.WeatherRepository
import com.example.mobile_app_project.viewmodel.WeatherViewModel
import com.example.mobile_app_project.viewmodel.SettingsViewModel

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
    val weatherViewModel = remember { WeatherViewModel(WeatherRepository(), UserPreferences(context)) }
    val settingsViewModel = remember { SettingsViewModel(UserPreferences(context)) }

    NavHost(navController = navController, startDestination = NavigationDestinations.HOME) {
        composable(NavigationDestinations.HOME) { HomeScreen(navController, weatherViewModel) }
        composable(
            route = NavigationDestinations.DETAIL + "?data={data}",
            arguments = listOf(
                navArgument("data") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val jsonData = backStackEntry.arguments?.getString("data") ?: ""
            DetailScreen(jsonData)
        }
        composable(NavigationDestinations.SETTINGS) { SettingsScreen(navController, settingsViewModel) }
    }
}
