package com.omersusin.cavepressor.domain.model

import androidx.compose.ui.graphics.Color

enum class AppTheme(
    val displayName: String,
    val seed: Color
) {
    CAVE("Cave", Color(0xFFD4A843)),
    DYNAMIC("Dynamic", Color.Transparent)
}
