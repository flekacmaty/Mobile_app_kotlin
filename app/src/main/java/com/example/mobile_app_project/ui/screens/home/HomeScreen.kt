package com.example.mobile_app_project.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile_app_project.data.local.UserPreferences
import com.example.mobile_app_project.ui.theme.CloudWhite
import com.example.mobile_app_project.ui.theme.SkyBlueLight
import com.example.mobile_app_project.ui.theme.TextDark
import com.example.mobile_app_project.ui.theme.TextSecondary
import com.example.mobile_app_project.viewmodel.WeatherViewModel
import java.util.Locale
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: WeatherViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current

    val requiredPermissions = remember {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
    var hasLocationPermission by remember { mutableStateOf(isLocationPermissionGranted(context)) }
    var permissionRequested by rememberSaveable { mutableStateOf(false) }
    var showRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.any { it.value }
        hasLocationPermission = granted
        if (!granted) {
            showRationale = requiredPermissions.any { permission ->
                activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permission) } == true
            }
        } else {
            showRationale = false
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                val grantedNow = isLocationPermissionGranted(context)
                hasLocationPermission = grantedNow
                showRationale = if (grantedNow) {
                    false
                } else {
                    requiredPermissions.any { permission ->
                        activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permission) } == true
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.loadWeatherForCurrentLocation(context)
        } else if (!permissionRequested) {
            permissionRequested = true
            permissionLauncher.launch(requiredPermissions)
        }
    }

    val permanentlyDenied = permissionRequested && !hasLocationPermission && !showRationale

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CloudWhite, SkyBlueLight)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Aktuální počasí",
                style = MaterialTheme.typography.titleLarge,
                color = TextDark
            )

            // Search input stays only for navigation to detail, not affecting Home data
            OutlinedTextField(
                value = uiState.cityName,
                onValueChange = { viewModel.onCityNameChange(it) },
                label = { Text("Město", color = TextDark) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextDark),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    cursorColor = TextDark,
                    focusedIndicatorColor = TextDark,
                    unfocusedIndicatorColor = TextDark,
                    focusedContainerColor = CloudWhite,
                    unfocusedContainerColor = CloudWhite,
                    disabledContainerColor = CloudWhite,
                    errorContainerColor = CloudWhite,
                    focusedLabelColor = TextDark,
                    unfocusedLabelColor = TextSecondary,
                    focusedPlaceholderColor = TextSecondary,
                    unfocusedPlaceholderColor = TextSecondary,
                    disabledPlaceholderColor = TextSecondary,
                    focusedSupportingTextColor = TextSecondary,
                    unfocusedSupportingTextColor = TextSecondary,
                    disabledTextColor = TextDark,
                    disabledIndicatorColor = TextSecondary
                )
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val name = uiState.cityName.trim()
                    if (name.isNotEmpty()) {
                        viewModel.searchAndNavigate(name, navController)
                    }
                }) { Text("Vyhledat počasí", color = TextDark) }
                Button(onClick = { viewModel.loadWeatherForCurrentLocation(context) }) { Text("Počasí zde", color = TextDark) }
            }

            if (!hasLocationPermission) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CloudWhite)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Abychom zobrazili počasí pro vaši polohu, potřebujeme přístup k lokalizaci.",
                            color = TextDark
                        )
                        val infoText = if (permanentlyDenied) {
                            "Oprávnění jste zakázali. Povolení můžete znovu udělit v nastavení aplikace."
                        } else if (showRationale) {
                            "Bez oprávnění nedokážeme získat aktuální polohu."
                        } else {
                            "Povolení je nutné pro načtení místního počasí."
                        }
                        Text(text = infoText, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!permanentlyDenied) {
                                Button(onClick = {
                                    permissionRequested = true
                                    permissionLauncher.launch(requiredPermissions)
                                }) {
                                    Text("Povolit polohu", color = TextDark)
                                }
                            }
                            Button(onClick = { openAppSettings(context) }) {
                                Text("Otevřít nastavení", color = TextDark)
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Načítám…", color = TextDark)
                }
            }

            uiState.errorMessage?.let { err ->
                Text(text = "Chyba: $err", color = MaterialTheme.colorScheme.error)
            }

            uiState.weatherData?.let { data ->
                if (!hasLocationPermission) return@let
                val prefs = UserPreferences(context)
                val tempUnit by prefs.observeTemperatureUnit().collectAsState(initial = "C")
                val windUnit by prefs.observeWindUnit().collectAsState(initial = "m_s")

                val displayTemp = if (tempUnit == "F") (data.currentTemperature ?: 0.0) * 9 / 5 + 32 else (data.currentTemperature ?: 0.0)
                val displayWind = if (windUnit == "km_h") ((data.currentWindSpeed ?: 0.0) * 3.6) else (data.currentWindSpeed ?: 0.0)
                val windLabel = if (windUnit == "km_h") "km/h" else "m/s"
                val tempLabel = if (tempUnit == "F") "°F" else "°C"

                Card(colors = CardDefaults.cardColors(containerColor = CloudWhite)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Teplota: ${String.format(Locale.getDefault(), "%.1f", displayTemp)} $tempLabel", color = TextDark)
                        Text(text = "Vítr: ${String.format(Locale.getDefault(), "%.1f", displayWind)} $windLabel", color = TextSecondary)
                    }
                }
            }
        }
    }
}

private fun isLocationPermissionGranted(context: Context): Boolean {
    val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
    val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
    return fineGranted || coarseGranted
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
