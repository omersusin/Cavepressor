package com.cavepressor.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CaveDarkColorScheme = darkColorScheme(
    primary = CavePrimary,
    onPrimary = CaveOnPrimary,
    primaryContainer = CavePrimaryContainer,
    onPrimaryContainer = CaveOnPrimaryContainer,
    secondary = CaveSecondary,
    onSecondary = CaveOnSecondary,
    secondaryContainer = CaveSecondaryContainer,
    onSecondaryContainer = CaveOnSecondaryContainer,
    tertiary = CaveTertiary,
    onTertiary = CaveOnTertiary,
    tertiaryContainer = CaveTertiaryContainer,
    onTertiaryContainer = CaveOnTertiaryContainer,
    error = CaveError,
    onError = CaveOnError,
    errorContainer = CaveErrorContainer,
    onErrorContainer = CaveOnErrorContainer,
    background = CaveBackground,
    onBackground = CaveOnBackground,
    surface = CaveSurface,
    onSurface = CaveOnSurface,
    surfaceVariant = CaveSurfaceVariant,
    onSurfaceVariant = CaveOnSurfaceVariant,
    outline = CaveOutline,
    outlineVariant = CaveOutlineVariant
)

private val CaveLightColorScheme = lightColorScheme(
    primary = CavePrimaryLight,
    onPrimary = CaveOnPrimaryLight,
    primaryContainer = CavePrimaryContainerLight,
    onPrimaryContainer = CaveOnPrimaryContainerLight,
    background = CaveBackgroundLight,
    onBackground = CaveOnBackgroundLight,
    surface = CaveSurfaceLight,
    onSurface = CaveOnSurfaceLight,
    surfaceVariant = CaveSurfaceVariantLight,
    onSurfaceVariant = CaveOnSurfaceVariantLight
)

@Composable
fun CavepressorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    val colorScheme = when {
        // Dynamic color sadece Android 12+ (API 31) destekler
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> CaveDarkColorScheme
        else -> CaveLightColorScheme
    }

    // Status bar rengi tema ile uyumlu olsun
    SideEffect {
        val window = (view.context as? android.app.Activity)?.window
        window?.let {
            WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CaveTypography,
        content = content
    )
}
