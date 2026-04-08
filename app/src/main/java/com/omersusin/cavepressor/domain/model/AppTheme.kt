package com.omersusin.cavepressor.domain.model

enum class AppTheme(
    val displayName: String,
    val seedArgb: Long
) {
    CAVE("Cave",    0xFFD4A843L),
    CRIMSON("Crimson", 0xFFDC143CL),
    VIOLET("Violet",  0xFF7C3AEDL),
    OCEAN("Ocean",   0xFF0EA5E9L),
    SAGE("Sage",    0xFF16A34AL),
    AMBER("Amber",   0xFFD97706L),
    ROSE("Rose",    0xFFEC4899L),
    MONO("Mono",    0xFF6B7280L)
}
