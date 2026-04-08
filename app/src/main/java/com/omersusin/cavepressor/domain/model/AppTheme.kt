package com.omersusin.cavepressor.domain.model

import androidx.compose.ui.graphics.Color
import com.omersusin.cavepressor.ui.theme.ThemeAmber
import com.omersusin.cavepressor.ui.theme.ThemeCave
import com.omersusin.cavepressor.ui.theme.ThemeCrimson
import com.omersusin.cavepressor.ui.theme.ThemeMono
import com.omersusin.cavepressor.ui.theme.ThemeOcean
import com.omersusin.cavepressor.ui.theme.ThemeRose
import com.omersusin.cavepressor.ui.theme.ThemeSage
import com.omersusin.cavepressor.ui.theme.ThemeViolet

enum class AppTheme(
    val displayName: String,
    val seed: Color
) {
    CAVE("Cave", ThemeCave),
    CRIMSON("Crimson", ThemeCrimson),
    VIOLET("Violet", ThemeViolet),
    OCEAN("Ocean", ThemeOcean),
    SAGE("Sage", ThemeSage),
    AMBER("Amber", ThemeAmber),
    ROSE("Rose", ThemeRose),
    MONO("Mono", ThemeMono),
    DYNAMIC("Dynamic", Color.Transparent)  // Wallpaper based
}
