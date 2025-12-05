package com.example.lingonary_.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. We removed 'Purple80' and act.
// 2. We use YOUR custom colors (SageGreen, etc.) defined in Color.kt

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    secondary = BadgeYellow,
    tertiary = SageGreen,
    background = TextBlack,
    surface = TextBlack
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    secondary = BadgeYellow,
    tertiary = SageGreen,
    background = BackgroundGray,
    surface = White,
    onPrimary = TextBlack,
    onSecondary = TextBlack,
    onTertiary = TextBlack,
    onBackground = TextBlack,
    onSurface = TextBlack
)

@Composable
fun Lingonary_Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}