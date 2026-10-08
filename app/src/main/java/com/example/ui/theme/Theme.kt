package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BrandNavy,
    onPrimary = TextInverse,
    primaryContainer = BrandNavyLight,
    onPrimaryContainer = TextInverse,
    secondary = BrandGold,
    onSecondary = BrandNavyDark,
    secondaryContainer = BrandGoldLight,
    onSecondaryContainer = BrandNavyDark,
    background = SurfaceBg,
    onBackground = TextPrimary,
    surface = CardBg,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandGold,
    onPrimary = BrandNavyDark,
    primaryContainer = BrandNavyLight,
    onPrimaryContainer = TextInverse,
    secondary = BrandGoldLight,
    onSecondary = BrandNavyDark,
    background = BrandNavyDark,
    onBackground = TextInverse,
    surface = BrandNavy,
    onSurface = TextInverse,
    surfaceVariant = BrandNavyLight,
    onSurfaceVariant = SurfaceBg,
    outline = BrandNavyLight
)

@Composable
fun WANTOKTripTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BrandNavyDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
