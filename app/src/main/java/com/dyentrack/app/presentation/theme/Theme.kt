package com.dyentrack.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    background = BackGroundColor,
    onBackground = PrimaryTextColor,

    surface = TertiaTwo,  // cartes
    onSurface = PrimaryTextColor,

    )


@Composable
fun DyeNTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content,
        shapes = Shapes,
    )
}