package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MyraCyan,
    onPrimary = MyraTextOnCyan,
    primaryContainer = MyraNavyLight,
    onPrimaryContainer = MyraCyan,
    secondary = MyraNeonViolet,
    onSecondary = MyraTextPrimary,
    secondaryContainer = MyraNavyMedium,
    onSecondaryContainer = MyraTextPrimary,
    tertiary = MyraElectricBlue,
    onTertiary = MyraTextPrimary,
    background = MyraBlack,
    onBackground = MyraTextPrimary,
    surface = MyraNavyDark,
    onSurface = MyraTextPrimary,
    surfaceVariant = MyraNavyMedium,
    onSurfaceVariant = MyraTextSecondary,
    outline = MyraNavyBorder,
    error = MyraNeonRed,
    onError = MyraBlack
)

@Composable
fun MYRAAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // MYRA AI features a tailored dark futuristic theme by design
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MyraBlack.toArgb()
            window.navigationBarColor = MyraBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
