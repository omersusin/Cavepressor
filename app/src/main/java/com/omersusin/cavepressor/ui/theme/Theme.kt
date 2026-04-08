package com.omersusin.cavepressor.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.omersusin.cavepressor.domain.model.AppTheme

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

// Seed renginden basit dark color scheme üret
private fun seedDarkColorScheme(seed: Color) = darkColorScheme(
    primary = seed,
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = seed.copy(alpha = 0.2f).compositeOver(Color(0xFF1A1A1A)),
    onPrimaryContainer = seed.copy(alpha = 0.9f),
    secondary = seed.copy(alpha = 0.7f),
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = seed.copy(alpha = 0.15f).compositeOver(Color(0xFF1A1A1A)),
    onSecondaryContainer = seed.copy(alpha = 0.8f),
    background = Color(0xFF0F0F0F),
    onBackground = Color(0xFFEEEEEE),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFEEEEEE),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = Color(0xFF666666),
    outlineVariant = Color(0xFF333333)
)

private fun seedLightColorScheme(seed: Color) = lightColorScheme(
    primary = seed,
    onPrimary = Color.White,
    primaryContainer = seed.copy(alpha = 0.15f).compositeOver(Color.White),
    onPrimaryContainer = seed.copy(alpha = 0.9f).compositeOver(Color.Black),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF111111),
    surface = Color.White,
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF444444)
)

private fun Color.compositeOver(background: Color): Color {
    val fg = this
    val a = fg.alpha
    return Color(
        red = fg.red * a + background.red * (1 - a),
        green = fg.green * a + background.green * (1 - a),
        blue = fg.blue * a + background.blue * (1 - a),
        alpha = 1f
    )
}

@Composable
fun CavepressorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    amoledMode: Boolean = false,
    appTheme: AppTheme = AppTheme.CAVE,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    var colorScheme = when {
        // 1. Dynamic color — Android 12+
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        // 2. Cave default
        appTheme == AppTheme.CAVE -> {
            if (darkTheme) CaveDarkColorScheme else CaveLightColorScheme
        }
        // 3. Seed based themes
        else -> {
            if (darkTheme) seedDarkColorScheme(appTheme.seed)
            else seedLightColorScheme(appTheme.seed)
        }
    }

    // AMOLED: background ve surface'i saf siyah yap
    if (amoledMode && darkTheme) {
        colorScheme = colorScheme.copy(
            background = AmoledBlack,
            surface = AmoledSurface,
            surfaceVariant = AmoledSurfaceVariant
        )
    }

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
