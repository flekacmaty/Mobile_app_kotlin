package com.example.mobile_app_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile_app_project.ui.screens.detail.DetailScreen
import com.example.mobile_app_project.ui.screens.home.HomeScreen
import com.example.mobile_app_project.ui.screens.settings.SettingsScreen

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
    NavHost(navController = navController, startDestination = NavigationDestinations.HOME) {
        composable(NavigationDestinations.HOME) { HomeScreen() }
        composable(NavigationDestinations.DETAIL) { DetailScreen() }
        composable(NavigationDestinations.SETTINGS) { SettingsScreen() }
    }
}
