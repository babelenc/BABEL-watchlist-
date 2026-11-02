package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CinemoraAccent,
    onPrimary = Color.Black,
    primaryContainer = CinemoraDarkCard,
    onPrimaryContainer = CinemoraAccent,
    secondary = CinemoraAccentLight,
    onSecondary = Color.Black,
    background = CinemoraDarkBg,
    onBackground = CinemoraTextPrimary,
    surface = CinemoraDarkSurface,
    onSurface = CinemoraTextPrimary,
    surfaceVariant = CinemoraDarkCard,
    onSurfaceVariant = CinemoraTextSecondary,
    outline = CinemoraBorder
)

private val LightColorScheme = lightColorScheme(
    primary = CinemoraAccentDark,
    onPrimary = Color.White,
    primaryContainer = CinemoraLightCard,
    onPrimaryContainer = CinemoraAccentDark,
    secondary = CinemoraAccent,
    onSecondary = Color.White,
    background = CinemoraLightBg,
    onBackground = CinemoraLightTextPrimary,
    surface = CinemoraLightSurface,
    onSurface = CinemoraLightTextPrimary,
    surfaceVariant = CinemoraLightCard,
    onSurfaceVariant = CinemoraLightTextSecondary,
    outline = CinemoraLightBorder
)

@Immutable
data class CinemoraColors(
    val cardBackground: Color,
    val cardVariant: Color,
    val border: Color,
    val borderActive: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val accentGlow: Color,
    val statusPlanToWatch: Color,
    val statusWatching: Color,
    val statusCompleted: Color,
    val statusOnHold: Color,
    val statusDropped: Color,
    val starGold: Color
)

val LocalCinemoraColors = staticCompositionLocalOf {
    CinemoraColors(
        cardBackground = CinemoraDarkCard,
        cardVariant = CinemoraDarkCardVariant,
        border = CinemoraBorder,
        borderActive = CinemoraBorderActive,
        textPrimary = CinemoraTextPrimary,
        textSecondary = CinemoraTextSecondary,
        textTertiary = CinemoraTextTertiary,
        accent = CinemoraAccent,
        accentGlow = CinemoraAccentGlow,
        statusPlanToWatch = StatusPlanToWatch,
        statusWatching = StatusWatching,
        statusCompleted = StatusCompleted,
        statusOnHold = StatusOnHold,
        statusDropped = StatusDropped,
        starGold = StarGold
    )
}

@Composable
fun CinemoraTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColors = if (darkTheme) {
        CinemoraColors(
            cardBackground = CinemoraDarkCard,
            cardVariant = CinemoraDarkCardVariant,
            border = CinemoraBorder,
            borderActive = CinemoraBorderActive,
            textPrimary = CinemoraTextPrimary,
            textSecondary = CinemoraTextSecondary,
            textTertiary = CinemoraTextTertiary,
            accent = CinemoraAccent,
            accentGlow = CinemoraAccentGlow,
            statusPlanToWatch = StatusPlanToWatch,
            statusWatching = StatusWatching,
            statusCompleted = StatusCompleted,
            statusOnHold = StatusOnHold,
            statusDropped = StatusDropped,
            starGold = StarGold
        )
    } else {
        CinemoraColors(
            cardBackground = CinemoraLightCard,
            cardVariant = Color(0xFFE8E8EE),
            border = CinemoraLightBorder,
            borderActive = CinemoraAccentDark.copy(alpha = 0.4f),
            textPrimary = CinemoraLightTextPrimary,
            textSecondary = CinemoraLightTextSecondary,
            textTertiary = Color(0xFF94A3B8),
            accent = CinemoraAccentDark,
            accentGlow = CinemoraAccentDark.copy(alpha = 0.2f),
            statusPlanToWatch = StatusPlanToWatch,
            statusWatching = StatusWatching,
            statusCompleted = StatusCompleted,
            statusOnHold = StatusOnHold,
            statusDropped = StatusDropped,
            starGold = StarGold
        )
    }

    CompositionLocalProvider(LocalCinemoraColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
