package com.example.mobile_app_project.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Navigation destinations and placeholder for future NavHost.
 */
object NavigationDestinations {
    const val HOME = "home"
    const val DETAIL = "detail"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavPlaceholder() {
    Text("Navigation Placeholder")
}

