package com.example.companyapp.ui.theme

import android.app.Activity
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

// Värit suoraan täällä tai voit tuoda Color.kt:sta
private val VioletDark = Color(0xFF673AB7)
private val VioletMedium = Color(0xBABA673AB7)
private val VioletLight = Color(0x54673AB7)
private val VioletPale = Color(0xFFEDE7F6)  // taustaväri vaaleateemaan

private val DarkColorScheme = darkColorScheme(
    primary = VioletDark,
    secondary = VioletMedium,
    tertiary = VioletLight,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF2B2930),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = VioletDark,
    secondary = VioletMedium,
    tertiary = VioletLight,
    background = Color.White,
    surface = VioletPale,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun CompanyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // ← vaihdettu true → false jotta omat värit näkyvät
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
        typography = AppTypography,  // ← vaihdettu Typography → AppTypography (Type.kt:sta)
        content = content
    )
}