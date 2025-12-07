package com.example.mobile_app_project.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = Color.Black,
    secondary = SoftSunset,
    onSecondary = Color.Black,
    tertiary = SunnyYellow,
    background = Color(0xFF0B1526),
    onBackground = Color.White,
    surface = Color(0xFF152033),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlueDeep,
    onPrimary = Color.White,
    secondary = SunnyYellow,
    onSecondary = Color.Black,
    tertiary = SoftSunset,
    onTertiary = Color.Black,
    background = CloudWhite,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark,
    outline = CardStroke
)

@Composable
fun Mobile_app_projectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}