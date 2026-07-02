package com.example.tasktracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PaleGreen,
    secondary = SoftMint,
    tertiary = PaleOrange,
    background = DeepForestBg,
    surface = DarkSageSurface,
    onPrimary = DeepForestBg,
    onSecondary = DeepForestBg,
    onTertiary = DeepForestBg,
    onBackground = LightClayText,
    onSurface = LightClayText,
    surfaceVariant = EarthyGrey,
    onSurfaceVariant = LightClayText
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    secondary = SageGreen,
    tertiary = WarmTerracotta,
    background = LightSandBg,
    surface = CreamSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkBarkText,
    onSurface = DarkBarkText,
    surfaceVariant = Color(0xFFE6DFD5),
    onSurfaceVariant = DarkBarkText
)

@Composable
fun TaskTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Turn off dynamic color to enforce our nature theme branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}