package com.example.mobile_app_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.mobile_app_project.ui.navigation.AppNavGraph
import com.example.mobile_app_project.ui.theme.Mobile_app_projectTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install Android 12+ SplashScreen
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Optional: keep splash for a short duration
        lifecycleScope.launch {
            delay(600)
        }

        setContent {
            Mobile_app_projectTheme {
                val navController = rememberNavController()
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavGraph(navController)
                }
            }
        }
    }
}
