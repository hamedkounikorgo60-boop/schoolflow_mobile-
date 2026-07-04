package com.schoolflow.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,
    secondary = Slate700,
    onSecondary = White,
    secondaryContainer = Slate100,
    onSecondaryContainer = Slate900,
    tertiary = Green600,
    onTertiary = White,
    tertiaryContainer = Green100,
    background = Slate50,
    onBackground = Slate900,
    surface = White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    outline = Slate200,
    error = Red600,
    onError = White,
    errorContainer = Red100,
    onErrorContainer = Red900
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue100,
    onPrimary = Blue900,
    primaryContainer = Blue700,
    onPrimaryContainer = White,
    secondary = Slate200,
    onSecondary = Slate900,
    background = Slate950,
    onBackground = White,
    surface = Slate900,
    onSurface = White,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate200,
    error = Red100,
    onError = Red900,
    errorContainer = Red900,
    onErrorContainer = Red100
)

@Composable
fun SchoolFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
