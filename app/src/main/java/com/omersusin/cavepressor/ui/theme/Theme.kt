package com.omersusin.cavepressor.ui.theme

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

enum class AppThemeType {
    CRIMSON, VIOLET, OCEAN, SAGE, AMBER, ROSE, MONO, CUSTOM
}

// Preset Dark Schemes matching the design provided
private fun getDarkColorScheme(themeType: AppThemeType, isAmoled: Boolean, customColor: Color) = darkColorScheme(
    primary = when (themeType) {
        AppThemeType.CRIMSON -> CrimsonPrimary
        AppThemeType.VIOLET -> VioletPrimary
        AppThemeType.OCEAN -> OceanPrimary
        AppThemeType.SAGE -> SagePrimary
        AppThemeType.AMBER -> AmberPrimary
        AppThemeType.ROSE -> RosePrimary
        AppThemeType.MONO -> MonoPrimary
        AppThemeType.CUSTOM -> customColor // User's custom settings
    },
    secondary = when (themeType) {
        AppThemeType.CRIMSON -> CrimsonSecondary
        AppThemeType.VIOLET -> VioletSecondary
        AppThemeType.OCEAN -> OceanSecondary
        AppThemeType.SAGE -> SageSecondary
        AppThemeType.AMBER -> AmberSecondary
        AppThemeType.ROSE -> RoseSecondary
        AppThemeType.MONO -> MonoSecondary
        AppThemeType.CUSTOM -> customColor.copy(alpha = 0.7f)
    },
    tertiary = when (themeType) {
        AppThemeType.CRIMSON -> CrimsonTertiary
        AppThemeType.VIOLET -> VioletTertiary
        AppThemeType.OCEAN -> OceanTertiary
        AppThemeType.SAGE -> SageTertiary
        AppThemeType.AMBER -> AmberTertiary
        AppThemeType.ROSE -> RoseTertiary
        AppThemeType.MONO -> MonoTertiary
        AppThemeType.CUSTOM -> customColor.copy(alpha = 0.5f)
    },
    background = if (isAmoled) AMOLEDBackground else when (themeType) {
        AppThemeType.CRIMSON -> CrimsonBackground
        AppThemeType.VIOLET -> VioletBackground
        AppThemeType.OCEAN -> OceanBackground
        AppThemeType.SAGE -> SageBackground
        AppThemeType.AMBER -> AmberBackground
        AppThemeType.ROSE -> RoseBackground
        AppThemeType.MONO -> MonoBackground
        AppThemeType.CUSTOM -> AMOLEDBackground
    },
    surface = if (isAmoled) AMOLEDSurface else when (themeType) {
        AppThemeType.CRIMSON -> CrimsonSurface
        AppThemeType.VIOLET -> VioletSurface
        AppThemeType.OCEAN -> OceanSurface
        AppThemeType.SAGE -> SageSurface
        AppThemeType.AMBER -> AmberSurface
        AppThemeType.ROSE -> RoseSurface
        AppThemeType.MONO -> MonoSurface
        AppThemeType.CUSTOM -> AMOLEDSurface
    },
    surfaceVariant = when (themeType) {
        AppThemeType.CRIMSON -> CrimsonSurfaceVariant
        AppThemeType.VIOLET -> VioletSurfaceVariant
        AppThemeType.OCEAN -> OceanSurfaceVariant
        AppThemeType.SAGE -> SageSurfaceVariant
        AppThemeType.AMBER -> AmberSurfaceVariant
        AppThemeType.ROSE -> RoseSurfaceVariant
        AppThemeType.MONO -> MonoSurfaceVariant
        AppThemeType.CUSTOM -> Color(0xFF333333)
    },
    error = CommonError,
    onError = CommonOnError,
    errorContainer = CommonErrorContainer,
    onErrorContainer = CommonOnErrorContainer
)

private fun getLightColorScheme(themeType: AppThemeType, customColor: Color) = lightColorScheme(
    primary = when (themeType) {
        AppThemeType.CRIMSON -> CrimsonPrimaryLight
        AppThemeType.VIOLET -> VioletPrimaryLight
        AppThemeType.OCEAN -> OceanPrimaryLight
        AppThemeType.SAGE -> SagePrimaryLight
        AppThemeType.AMBER -> AmberPrimaryLight
        AppThemeType.ROSE -> RosePrimaryLight
        AppThemeType.MONO -> MonoPrimaryLight
        AppThemeType.CUSTOM -> customColor
    },
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    error = CommonError,
    onError = CommonOnError,
    errorContainer = CommonErrorContainer,
    onErrorContainer = CommonOnErrorContainer
)

@Composable
fun CavepressorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    isAmoledMode: Boolean = false,
    themeType: AppThemeType = AppThemeType.SAGE,
    customColorValue: Int = android.graphics.Color.GREEN,
    content: @Composable () -> Unit
) {
    val cColor = Color(customColorValue)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> getDarkColorScheme(themeType, isAmoledMode, cColor)
        else -> getLightColorScheme(themeType, cColor)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CaveTypography,
        content = content
    )
}