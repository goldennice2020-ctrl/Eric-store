package com.golden.earthol.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightScheme = lightColorScheme(
    primary = HudPrimary,
    secondary = HudSecondary,
    background = HudBackground,
    surface = HudSurface,
    surfaceVariant = HudSurfaceAlt,
    error = HudDanger,
    onPrimary = HudBackground,
    onSecondary = HudBackground,
    onBackground = HudText,
    onSurface = HudText,
    onSurfaceVariant = HudMuted
)

@Composable
fun EarthOLTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = EarthTypography,
        content = content
    )
}
